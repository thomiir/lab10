package transportAgency.protobufprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProtoClientWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;
    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;
    private static Logger logger = LogManager.getLogger(ProtoClientWorker.class);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");


    public ProtoClientWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
            input = connection.getInputStream();
            connected = true;
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void run() {
        while (connected) {
            try {
                logger.info("waiting for requests");
                Request request = Request.parseDelimitedFrom(input);
                Response response = handleRequest(request);
                if (response != null) sendResponse(response);
            } catch (IOException e) {
                logger.error(e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private synchronized void sendResponse(Response response) {
        try {
            logger.info("Sending response{}", response.getType());
            response.writeDelimitedTo(output);
            output.flush();
        }
        catch (IOException e) {
            logger.error(e);
        }
    }

    private Response handleRequest(Request request) {
        switch (request.getType()) {
            case Login: {
                logger.debug("Login request...");
                Employee employee = request.getEmployee();
                try {
                    server.login(employee.getUsername(), employee.getPassword(), this);
                    return ProtobufUtils.createOkResponse();
                } catch (java.lang.Exception e) {
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case Logout: {
                logger.debug("Logout request...");
                try {
                    server.logout(new transportAgency.model.Employee(-1L, request.getEmployee().getUsername(), request.getEmployee().getPassword()), this);
                    connected = false; ///?
                    return ProtobufUtils.createOkResponse();
                } catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case FindSeats: {
                logger.debug("FindSeatsRequest...");
                try {
                    Trip trip = request.getTrip();
                    // datetimeformatter
                    Date date = Date.valueOf(LocalDate.parse(trip.getDepartureDate(), dateFormatter));
                    Time time = Time.valueOf(LocalTime.parse(trip.getDepartureTime(), timeFormatter));
                    System.out.println("[PROTOWORKER]: " + trip.getDestination() + " " + date + " " + time);
                    Seat[] seats = server.findAllReservedSeats(trip.getDestination(), date, time);
                    return ProtobufUtils.createFindSeatsResponse(seats);
                } catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case FindTrips: {
                logger.debug("FindTripsRequest...");
                try {
                    transportAgency.model.Trip[] trips = server.getAllTrips();
                    return ProtobufUtils.createFindTripsResponse(trips);
                } catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case MakeReservation: {
                try {
                    logger.debug("MakeReservationRequest...");
                    transportAgency.protobufprotocol.Reservation reservation = request.getReservation();
                    Date date = Date.valueOf(LocalDate.parse(reservation.getTrip().getDepartureDate(), dateFormatter));
                    Time time = Time.valueOf(LocalTime.parse(reservation.getTrip().getDepartureTime(), timeFormatter));
                    transportAgency.model.Trip trip = new transportAgency.model.Trip(-1L, reservation.getTrip().getDestination(), date, time, 0);
                    server.makeReservation(reservation.getClientName(), reservation.getNoSeats(), trip);
                    return ProtobufUtils.createOkResponse();
                }
                catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case FindEmployee: {
                try {
                    logger.debug("FindEmployeeRequest");
                    transportAgency.model.Employee employee = server.getEmployeeByUsername(request.getEmployee().getUsername());
                    return ProtobufUtils.createFindEmployeeResponse(employee);
                }
                catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }

            case FindTrip: {
                try {
                    logger.debug("FindTripRequest");
                    Date date = Date.valueOf(LocalDate.parse(request.getTrip().getDepartureDate(), dateFormatter));
                    Time time = Time.valueOf(LocalTime.parse(request.getTrip().getDepartureTime(), timeFormatter));
                    transportAgency.model.Trip trip = server.findTripByDestinationDateTime(request.getTrip().getDestination(), date, time);
                    return ProtobufUtils.createFindTripResponse(trip);
                }
                catch (Exception e) {
                    logger.error(e);
                    return ProtobufUtils.createErrorResponse(e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public void reservationMade(Reservation reservation) {
        logger.debug("reservation made {}", reservation);
        sendResponse(ProtobufUtils.createMakeReservationResponse(reservation));
    }
}
//protoc -I=C:\Users\baroa\OneDrive\Desktop\MPP\java\demo\proto --java_out=javaFiles --csharp_out=csharpFiles C:\Users\baroa\OneDrive\Desktop\MPP\java\demo\proto\ProtoDef.proto