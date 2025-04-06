package transportAgency.objectprotocol;

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
import java.net.InetSocketAddress;
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

//    private static Logger logger = LogManager.getLogger(ServicesProxy.class);

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() {
        try {
            connection = new Socket();
            connection.connect(new InetSocketAddress(host, port), 5000);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error(e);
//            logger.error(e.getStackTrace());
        }
    }

    private void closeConnection() {
        finished = true;
        System.out.println("connection closed");
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error(e);
//            logger.error(e.getStackTrace());
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error sending object " + e);
        }
    }

    private Response readResponse() {
        System.out.println("in readREsponse");
        Response response = null;
        try{
            System.out.println("before take");
            System.out.println(qresponses.size());
            response = qresponses.take();
            System.out.println("after take");
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error(e);
//            logger.error(e.getStackTrace());
        }
        return response;
    }

    private void startReader() {
        try {
            Thread tw = new Thread(new ReaderThread());
            tw.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean login(String username, String password, IObserver client) throws Exception {
        initializeConnection();
        EmployeeDTO employeeDTO = new EmployeeDTO(username, password);
        sendRequest(new LoginRequest(employeeDTO));
        Response response = readResponse();
        if (response instanceof OkResponse) {
            this.client = client;
            return true;
        } else if (response instanceof ErrorResponse) {
            closeConnection();
            throw new Exception(((ErrorResponse) response).getMessage());
        }
        return false;
    }

    @Override
    public void logout(Employee employee, IObserver client) throws Exception {
        EmployeeDTO employeeDTO = DTOUtils.getDTO(employee);
        sendRequest(new LogoutRequest(employeeDTO));
        Response response = readResponse();
        closeConnection();
        if (response instanceof ErrorResponse) {
            throw new Exception(((ErrorResponse) response).getMessage());
        }
    }

    @Override
    public Employee getEmployee(String username, String password) throws Exception {
        return DTOUtils.getFromDTO(new EmployeeDTO(username, password));
    }

    @Override
    public Seat[] findAllReservedSeats(String destination, String date, String time) throws Exception {
        TripDTO tripDTO = new TripDTO(-1L, destination, Date.valueOf(date), Time.valueOf(time), 18);
        sendRequest(new FindSeatsRequest(tripDTO));
        Response response = readResponse();
        if (response instanceof ErrorResponse) {
            throw new Exception(((ErrorResponse) response).getMessage());
        }
        FindSeatsResponse findReservedSeatsResponse = (FindSeatsResponse) response;
        SeatDTO[] seatDTOs = findReservedSeatsResponse.seats();
        return DTOUtils.getFromDTO(seatDTOs);
    }

    @Override
    public void makeReservation(String clientName, Integer noSeats, Trip trip) throws Exception {
        System.out.println("Making reservation - client: " + clientName + ", seats: " + noSeats);
        try {
            TripDTO tripDTO = DTOUtils.getDTO(trip);
            ReservationDTO reservationDTO = new ReservationDTO(clientName, noSeats, tripDTO);

            System.out.println("Sending reservation request");
            sendRequest(new MakeReservationRequest(reservationDTO));

            System.out.println("Waiting for reservation response");
            UpdateResponse response = (UpdateResponse) readResponse();
            System.out.println(response == null);
            if (response == null) {
                throw new Exception("No response received for reservation request");
            }

            System.out.println("Received response: " + response.getClass().getSimpleName());
            if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }

            if (response instanceof MakeReservationResponse) {
                MakeReservationResponse makeReservationResponse = (MakeReservationResponse) response;
                Reservation reservation = DTOUtils.getFromDTO(makeReservationResponse.reservationDTO());
                client.reservationMade(reservation);
                System.out.println("Reservation confirmed");
            } else {
                System.err.println("Unexpected response type: " + response.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("Error making reservation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        System.out.println("makeReservation completed");
    }
    @Override
    public Trip[] getAllTrips() throws Exception {
        sendRequest(new FindTripsRequest());
        Response response = readResponse();
        if (response instanceof ErrorResponse) {
            throw new Exception(((ErrorResponse) response).getMessage());
        }
        FindTripsResponse findTripsResponse = (FindTripsResponse) response;;
        TripDTO[] tripDTOs = findTripsResponse.trips();
        return DTOUtils.getFromDTO(tripDTOs);
    }

    @Override
    public long getId(String username, String password) throws Exception {
        return 0;
    }

    @Override
    public Trip findTrip(String destination, Date departureDate, Time departureTime) throws Exception {
        return null;
    }

    @Override
    public Trip findTripById(Long id) throws Exception {
        return null;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
//                    logger.debug("response received {}",response);
                    System.out.println(response instanceof UpdateResponse);
                    if (response instanceof UpdateResponse){
//                        handleUpdate((UpdateResponse)response);
                        System.out.println("exit handle update");
                        qresponses.put((UpdateResponse)response);
                    }else{
                        try {
                            qresponses.put((Response)response);
                        } catch (Exception e) {
                            e.printStackTrace();
//                            logger.error(e);
//                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    logger.error("Reading error "+e);
                }
            }
        }
    }

    private void handleUpdate(UpdateResponse response) {
        System.out.println("entered handle update");
        if (response instanceof MakeReservationResponse) {
            System.out.println("entered if");
            MakeReservationResponse makeReservationResponse = (MakeReservationResponse) response;
            System.out.println("casted response");
            Reservation reservation = DTOUtils.getFromDTO(makeReservationResponse.reservationDTO());
            System.out.println(reservation);
            try {
                System.out.println("enterd try");
                System.out.println(client);
                client.reservationMade(reservation);
                System.out.println("exit try");
                //                logger.debug("reservation made {}", makeReservationResponse);
            } catch (Exception e) {
                e.printStackTrace();
//                logger.error(e);
//                logger.error(e.getStackTrace());
            }
        }
    }
}
