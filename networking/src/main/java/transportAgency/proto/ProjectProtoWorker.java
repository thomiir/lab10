package transportAgency.proto;

import transportAgency.model.Employee;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ProjectProtoWorker implements IObserver{
    private IServices server;
    private Socket connection;

    private InputStream inputStream;
    private OutputStream outputStream;
    private volatile boolean connected;

    public ProjectProtoWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            inputStream = connection.getInputStream();
            outputStream = connection.getOutputStream();
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                ProtocolProto.Request request = ProtocolProto.Request.parseDelimitedFrom(inputStream);
                ProtocolProto.Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(ProtocolProto.Response response) {
        System.out.println("Sending response ...");
        try {
            response.writeDelimitedTo(outputStream);
            outputStream.flush();
            System.out.println("Response sent ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProtocolProto.Response handleRequest(ProtocolProto.Request request) {
        ProtocolProto.Response response = null;
        if (request.getType() == ProtocolProto.Request.RequestType.LOGIN) {
            System.out.println("Login request ...");
            try {
                synchronized (server) {
                    if (!server.login(request.getAgentie().getUsername(), request.getAgentie().getPass(), this))
                        throw new Exception("Nume sau parola gresite!");
                    server.addObserver(this);
                }
                System.out.println("Login OK");
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
            } catch (Exception e) {
                connected = false;
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.LOGOUT) {
            System.out.println("Logout request...");
            try {
                synchronized (server) {
                    Employee ag = new Employee(-1L, request.getAgentie().getUsername(), "");
                    ag.setId(request.getAgentie().getId());
                    server.logout(ag, this);
                }
                connected = false;
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.GET_EXCURSII) {
            System.out.println("All excursii request");
            try {
                synchronized (server) {
                    List<Trip> excursii = server.getAllTrips();
                    ProtocolProto.Response.Builder builder = ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.EXCURSII);
                    for (Trip excursie : excursii) {
                        builder.addExcursii(ProtocolProto.Trip.newBuilder()
                                .setId(excursie.getId())
                                .setDestination(excursie.getDestination())
                                .setDepartureDate(excursie.getDepartureDate().toString())
                                .setDepartureTime(excursie.getDepartureTime().toString())
                                .setNoSeatsAvailable(excursie.getNoSeatsAvailable())
                                .build());
                    }
                    return builder.build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }
        if (request.getType() == ProtocolProto.Request.RequestType.GET_ID) {
            try {
                synchronized (server) {
                    long id = server.getId(request.getAgentie().getUsername(), request.getAgentie().getPass());
                    return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).setAgentie(
                            ProtocolProto.Employee.newBuilder().setId(id).build()
                    ).build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.UPDATE_SEARCH) {

        }

        if (request.getType() == ProtocolProto.Request.RequestType.ADD_REZ) {
            try {
                synchronized (server) {
                    server.makeReservation(request.getRezervare().getClientName(), request.getRezervare().getNoSeats(), request.getRezervare().getTripId());
                    return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.OK).build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder().setType(ProtocolProto.Response.ResponseType.ERROR).setError(e.getMessage()).build();
            }
        }

        if (request.getType() == ProtocolProto.Request.RequestType.GET_FREE_SEATS) {
            System.out.println("Get reserved seats request");
            try {
                synchronized (server) {
                    // Extract parameters
                    String destination = request.getExcursie().getDestination();
                    String departureDate = request.getExcursie().getDepartureDate();
                    String departureTime = request.getExcursie().getDepartureTime();

                    System.out.println("Finding seats for: " + destination + " on " + departureDate + " at " + departureTime);
                    List<Seat> seatList = server.findAllReservedSeats(destination, departureDate, departureTime);
                    System.out.println(seatList.size()); // e corect pana aici
                    ProtocolProto.Response.Builder responseBuilder = ProtocolProto.Response.newBuilder()
                            .setType(ProtocolProto.Response.ResponseType.OK);

                    for (Seat seat : seatList) {
                        ProtocolProto.Seat.Builder seatBuilder = ProtocolProto.Seat.newBuilder()
                                .setSeatNo(seat.seatNo())  // Make sure this is a long value
                                .setClientName(seat.clientName() != null ? seat.clientName() : "");

                        responseBuilder.addSeats(seatBuilder.build());  // Note: adding to the 'seats' repeated field
                    }

                    return responseBuilder.build();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ProtocolProto.Response.newBuilder()
                        .setType(ProtocolProto.Response.ResponseType.ERROR)
                        .setError("Error getting seats: " + e.getMessage())
                        .build();
            }
        }
        if (request.getType() == ProtocolProto.Request.RequestType.GET_EMPLOYEE) {
            try {
                synchronized (server) {
                    Employee emp = server.getEmployee(request.getAgentie().getUsername(), request.getAgentie().getPass());
                    if (emp == null) {
                        return ProtocolProto.Response.newBuilder()
                                .setType(ProtocolProto.Response.ResponseType.ERROR)
                                .setError("Employee not found")
                                .build();
                    }
                    ProtocolProto.Employee protoEmp = ProtocolProto.Employee.newBuilder()
                            .setId(emp.getId())
                            .setUsername(emp.getUsername())
                            .setPass(emp.getPassword())
                            .build();
                    return ProtocolProto.Response.newBuilder()
                            .setType(ProtocolProto.Response.ResponseType.OK)
                            .setAgentie(protoEmp)
                            .build();
                }
            } catch (Exception e) {
                return ProtocolProto.Response.newBuilder()
                        .setType(ProtocolProto.Response.ResponseType.ERROR)
                        .setError("Error retrieving employee: " + e.getMessage())
                        .build();
            }
        }

        return response;
    }

    @Override
    public void reservationMade() throws transportAgency.services.Exception {

    }
}
