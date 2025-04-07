package transportAgency.objectprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.dto.*;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesProxy implements IServices {
    private String host;
    private int port;
    private IObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private static Logger logger = LogManager.getLogger(ServicesProxy.class);
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() {
        if (connection != null && connection.isConnected()) return;
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            synchronized (output) {
                output.writeObject(request);
                output.flush();
                output.reset();
            }
        } catch (IOException e) {
            logger.error(e);
            throw new Exception("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try{
            response = qresponses.take();
        } catch (Exception e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }

    private void startReader() {
        try {
            Thread tw = new Thread(new ReaderThread());
            tw.start();
        }
        catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void login(String username, String password, IObserver client) throws Exception {
        initializeConnection();
        EmployeeDTO employeeDTO = new EmployeeDTO(username, password);
        sendRequest(new LoginRequest(employeeDTO));
        Response response = readResponse();
        if (response instanceof OkResponse)
            this.client = client;
        else if (response instanceof ErrorResponse) {
            logger.error(((ErrorResponse) response).getMessage());
            throw new Exception(((ErrorResponse) response).getMessage());
        }
    }

    @Override
    public void logout(Employee employee, IObserver client) {
        try {
            EmployeeDTO employeeDTO = DTOUtils.getDTO(employee);
            sendRequest(new LogoutRequest(employeeDTO));
            Response response = readResponse();
            if (response instanceof ErrorResponse)
            {
                logger.error(((ErrorResponse) response).getMessage());
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            closeConnection();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public Employee getEmployee(String username, String password) {
        return DTOUtils.getFromDTO(new EmployeeDTO(username, password));
    }

    @Override
    public Seat[] findAllReservedSeats(String destination, String date, String time) throws Exception {
        TripDTO tripDTO = new TripDTO(-1L, destination, Date.valueOf(date), Time.valueOf(time), 18);
        sendRequest(new FindSeatsRequest(tripDTO));
        Response response = readResponse();
        if (response instanceof ErrorResponse) {
            logger.error(((ErrorResponse) response).getMessage());
            throw new Exception(((ErrorResponse) response).getMessage());
        }
        FindSeatsResponse findReservedSeatsResponse = (FindSeatsResponse) response;
        SeatDTO[] seatDTOs = findReservedSeatsResponse.seats();
        return DTOUtils.getFromDTO(seatDTOs);
    }

    @Override
    public void makeReservation(String clientName, Integer noSeats, Trip trip) {
        try {
            TripDTO tripDTO = DTOUtils.getDTO(trip);
            ReservationDTO reservationDTO = new ReservationDTO(clientName, noSeats, tripDTO);
            sendRequest(new MakeReservationRequest(reservationDTO));
            Response response = readResponse();
            if (response instanceof ErrorResponse) {
                logger.error(((ErrorResponse) response).getMessage());
                throw new Exception(((ErrorResponse) response).getMessage());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public Trip[] getAllTrips() throws Exception {
        sendRequest(new FindTripsRequest());
        Response response = readResponse();
        if (response instanceof ErrorResponse) {
            logger.error(((ErrorResponse) response).getMessage());
            throw new Exception(((ErrorResponse) response).getMessage());
        }
        FindTripsResponse findTripsResponse = (FindTripsResponse) response;
        TripDTO[] tripDTOs = findTripsResponse.trips();
        return DTOUtils.getFromDTO(tripDTOs);
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    if (response instanceof UpdateResponse){
                        handleUpdate((UpdateResponse)response);
                    }else if (response instanceof Response){
                        try {
                            qresponses.put((Response)response);
                        } catch (Exception e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Reading error {}", String.valueOf(e));
                }
            }
        }
    }

    private void handleUpdate(UpdateResponse response) {
        if (response instanceof MakeReservationResponse) {
            try {
                MakeReservationResponse makeReservationResponse = (MakeReservationResponse) response;
                ReservationDTO reservationDTO = makeReservationResponse.rdto();
                Reservation reservation = DTOUtils.getFromDTO(reservationDTO);
                synchronized (client) {
                    client.reservationMade(reservation);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
}
