package transportAgency.objectprotocol;

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

//    private static Logger logger = LogManager.getLogger(ClientWorker.class);

    public ClientWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
//             logger.error(e);
//             logger.error(e.getStackTrace());
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
                e.printStackTrace();
//                logger.error(e);
//                logger.error(e.getStackTrace());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
////                logger.error(e);
////                logger.error(e.getStackTrace());
//            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error("Error "+e);
        }

    }

    private Response handleRequest(Request request) {
        Response response = null;

        if (request instanceof LoginRequest) {
//            logger.debug("Login request...");
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
            // logger.debug("Logout request...");
            LogoutRequest logoutRequest = (LogoutRequest) request;
            EmployeeDTO employeeDTO = logoutRequest.getEmployee();
            Employee employee = DTOUtils.getFromDTO(employeeDTO);
            try {
                server.logout(employee, this);
                connected = false;
                return new OkResponse();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof MakeReservationRequest) {
            try {
                // logger.debug("MakeReservationRequest...");
                MakeReservationRequest makeReservationRequest = (MakeReservationRequest) request;
                ReservationDTO reservationDTO = makeReservationRequest.reservation();
                Reservation reservation = DTOUtils.getFromDTO(reservationDTO);
                try {
                    System.out.println("worker before make reservation");
                    server.makeReservation(reservation.getClientName(), reservation.getNoSeats(), reservation.getTrip());
                    System.out.println("worker after make reservation");
                    return new MakeReservationResponse(reservationDTO);
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return new ErrorResponse(e.getMessage());
                }
            }
            catch (java.lang.Exception e) {
                e.printStackTrace();
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof FindSeatsRequest) {
            // logger.debug("FindSeatsRequest...");
            try {
                FindSeatsRequest findSeatsRequest = (FindSeatsRequest) request;
                TripDTO tripDTO = findSeatsRequest.trip();
                Trip trip = DTOUtils.getFromDTO(tripDTO);
                Seat[] seats = server.findAllReservedSeats(trip.getDestination(), trip.getDepartureDate().toString(), trip.getDepartureTime().toString());
                SeatDTO[] seatDTOs = DTOUtils.getDTO(seats);
                return new FindSeatsResponse(seatDTOs);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return new ErrorResponse(e.getMessage());
            }
        }

        if (request instanceof FindTripsRequest) {
            // logger.debug("FindTripsRequest...");
            try {
                Trip[] trips = server.getAllTrips();
                TripDTO[] tripDTOs = DTOUtils.getDTO(trips);
                return new FindTripsResponse(tripDTOs);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return new ErrorResponse(e.getMessage());
            }
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException {
//        logger.debug("sending response {}", response);
        synchronized (output) {
            try {
                System.out.println("response sending");
                output.writeObject(response);
                output.flush();
                System.out.println("response sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reservationMade(Reservation reservation) throws Exception {
        ReservationDTO reservationDTO = DTOUtils.getDTO(reservation);
//        logger.debug("reservation made {}", reservationDTO);
        try {
            sendResponse(new MakeReservationResponse(reservationDTO));
        } catch (IOException e) {
            e.printStackTrace();
//            logger.error(e);
//            logger.error(e.getStackTrace());
        }
    }
}
