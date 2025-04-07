package transportAgency.objectprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.dto.*;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.services.Exception;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(ClientWorker.class);

    public ClientWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
             logger.error(e);
             logger.error(e.getStackTrace());
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Object response = handleRequest((Request)request);
                if (response != null){
                    sendResponse((Response) response);
                }
            } catch (IOException e) {
                connected = false;
                logger.error(e);
                logger.error(e.getStackTrace());
            } catch (ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private Response handleRequest(Request request) {
        if (request instanceof LoginRequest) {
            logger.debug("Login request...");
            LoginRequest loginRequest = (LoginRequest) request;
            EmployeeDTO employeeDTO = loginRequest.employee();
            Employee employee = DTOUtils.getFromDTO(employeeDTO);
            try {
                server.login(employee.getUsername(), employee.getPassword(), this);
                return new OkResponse();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof LogoutRequest) {
             logger.debug("Logout request...");
            LogoutRequest logoutRequest = (LogoutRequest) request;
            EmployeeDTO employeeDTO = logoutRequest.getEmployee();
            Employee employee = DTOUtils.getFromDTO(employeeDTO);
            try {
                server.logout(employee, this);
                connected = false; ///?
                return new OkResponse();
            } catch (java.lang.Exception e) {
                logger.error(e);
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof MakeReservationRequest) {
            try {
                 logger.debug("MakeReservationRequest...");
                MakeReservationRequest makeReservationRequest = (MakeReservationRequest) request;
                ReservationDTO reservationDTO = makeReservationRequest.reservation();
                Reservation reservation = DTOUtils.getFromDTO(reservationDTO);
                try {
                    server.makeReservation(reservation.getClientName(), reservation.getNoSeats(), reservation.getTrip());
                    return new OkResponse();
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return new ErrorResponse(e.getMessage());
                }
            }
            catch (java.lang.Exception e) {
                logger.error(e);
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof FindSeatsRequest) {
             logger.debug("FindSeatsRequest...");
            try {
                FindSeatsRequest findSeatsRequest = (FindSeatsRequest) request;
                TripDTO tripDTO = findSeatsRequest.trip();
                Trip trip = DTOUtils.getFromDTO(tripDTO);
                Seat[] seats = server.findAllReservedSeats(trip.getDestination(), trip.getDepartureDate().toString(), trip.getDepartureTime().toString());
                SeatDTO[] seatDTOs = DTOUtils.getDTO(seats);
                return new FindSeatsResponse(seatDTOs);
            } catch (java.lang.Exception e) {
                logger.error(e);
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof FindTripsRequest) {
             logger.debug("FindTripsRequest...");
            try {
                Trip[] trips = server.getAllTrips();
                TripDTO[] tripDTOs = DTOUtils.getDTO(trips);
                return new FindTripsResponse(tripDTOs);
            } catch (java.lang.Exception e) {
                logger.error(e);
                return new ErrorResponse(e.getMessage());
            }
        }
        return null;
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("sending response {}", response);
        synchronized (output) {
            try {
                output.writeObject(response);
                output.flush();
                output.reset();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void reservationMade(Reservation reservation) {
        logger.debug("reservation made {}", reservation);
        ReservationDTO reservationDTO = DTOUtils.getDTO(reservation);
        try {
            sendResponse(new MakeReservationResponse(reservationDTO));
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
