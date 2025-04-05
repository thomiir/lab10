package transportAgency.proto;

import transportAgency.model.Employee;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.rpcprotocol.Response;
import transportAgency.rpcprotocol.ResponseType;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProjectServerProxy implements IServices {
    private String host;
    private int port;
    private volatile IObserver client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;
    private Queue<ProtocolProto.Response> responses;
    private volatile boolean finished;
    private final Lock lock = new ReentrantLock();
    private final Condition responseAvailable = lock.newCondition();

    public ProjectServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedList<>();
    }

    private void closeConnection() {
        finished = true;
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(ProtocolProto.Request request) {
        lock.lock();
        try {
            //outputStream.writeObject(request);
            request.writeDelimitedTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private ProtocolProto.Response readResponse() {
        lock.lock();
        try {
            while (responses.isEmpty()) {
                responseAvailable.await();
            }
            return responses.poll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    private void initializeConnection() {
        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        new Thread(this::run).start();
    }

    public void run() {
        while (!finished) {
            try {
                ProtocolProto.Response response = ProtocolProto.Response.parseDelimitedFrom(inputStream);
                if (response == null) {
                    // Connection closed
                    finished = true;
                    break;
                }

                if (response.getType() == ProtocolProto.Response.ResponseType.ADDED_REZERVATION) {
                    handleReservationUpdate(response);
                } else {
                    handleRegularResponse(response);
                }
            } catch (IOException e) {
                if (!finished) {
                    System.out.println("Reading error " + e);
                }
                finished = true;
            }
        }
    }


    @Override
    public Boolean login(String username, String password, IObserver client) {
        initializeConnection();
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.LOGIN)
                .setAgentie(ProtocolProto.Employee.newBuilder()
                        .setId(1)
                        .setUsername(username)
                        .setPass(password)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        if (response.getType() == ProtocolProto.Response.ResponseType.OK) {
            this.client = client;
            return true;
        }
        if (response.getType() == ProtocolProto.Response.ResponseType.ERROR) {
            closeConnection();
            return false;
        }
        return false;
    }


    @Override
    public void logout(Employee user, IObserver client) {
        System.out.println("Logout request...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.LOGOUT)
                .setAgentie(ProtocolProto.Employee.newBuilder()
                        .setId(1)
                        .setUsername(user.getUsername())
                        .setPass("")
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        closeConnection();
        if (response.getType() == ProtocolProto.Response.ResponseType.ERROR) {
            throw new RuntimeException("Error logging out" + response.getError());
        }
    }

    @Override
    public Employee getEmployee(String username, String password) throws Exception {
        ProtocolProto.Request request = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_EMPLOYEE)
                .setAgentie(ProtocolProto.Employee.newBuilder()
                        .setUsername(username)
                        .setPass(password)
                        .build())
                .build();
        sendRequest(request);
        ProtocolProto.Response response = readResponse();
        assert response != null;
        if (response.getType() == ProtocolProto.Response.ResponseType.OK) {
            ProtocolProto.Employee emp = response.getAgentie();
            return new Employee(emp.getId(), emp.getUsername(), emp.getPass());
        } else if (response.getType() == ProtocolProto.Response.ResponseType.ERROR) {
            throw new Exception("Error retrieving employee: " + response.getError());
        }
        return null;
    }

    @Override
    public List<Trip> getAllTrips() {
        System.out.println("Get all excursii request ...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_EXCURSII)
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        List<Trip> excursii = new ArrayList<>();
        for (ProtocolProto.Trip excursie : response.getExcursiiList()) {
            excursii.add(new Trip(excursie.getId(),
                    excursie.getDestination(),
                    Date.valueOf(excursie.getDepartureDate()),
                    Time.valueOf(excursie.getDepartureTime()),
                    excursie.getNoSeatsAvailable()));
        }
        return excursii;
    }

    @Override
    public Trip findTrip(String destination, Date departureDate, Time departureTime) throws Exception {
        return null;
    }

    @Override
    public Trip findTripById(Long id) throws Exception {
        return null;
    }

    private void handleReservationUpdate(ProtocolProto.Response response) {
        synchronized (this) {
            if (client != null) {
                try {
                    client.reservationMade();
                } catch (Exception e) {
                    System.err.println("Error notifying client: " + e.getMessage());
                }
            } else {
                System.err.println("No client registered to notify");
            }
        }
    }

    private void handleRegularResponse(ProtocolProto.Response response) {
        lock.lock();
        try {
            responses.offer(response);
            responseAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addObserver(IObserver observer) {

    }

    @Override
    public void removeObserver(IObserver observer) {

    }

    @Override
    public void notifyObservers() {

    }

    @Override
    public List<Seat> findAllReservedSeats(String destination, String date, String time) {
        System.out.println("Find seats request...");
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.GET_FREE_SEATS)
                .setExcursie(ProtocolProto.Trip.newBuilder()
                    .setDestination(destination)
                        .setDepartureDate(date)
                        .setDepartureTime(time).build()).build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        List<Seat> seats = new ArrayList<>();
        System.out.println(response.getSeatsList().size());
        for (ProtocolProto.Seat seat : response.getSeatsList()) {
            seats.add(new Seat(seat.getSeatNo(), seat.getClientName()));
        }
        return seats;
    }

    @Override
    public void makeReservation(String clientName, Integer noSeats, Long tripId) {
        System.out.println("Add rezervare request ...");
        System.out.println("proxy tripId" + tripId);
        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
                .setType(ProtocolProto.Request.RequestType.ADD_REZ)
                .setRezervare(ProtocolProto.Reservation.newBuilder()
                        .setId(-1L)
                        .setClientName(clientName)
                        .setNoSeats(noSeats)
                        .setTripId(tripId)
                        .build())
                .build();
        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        System.out.println(response);
    }

    @Override
    public long getId(String username, String password) {
        System.out.println("Get id request ...");
//        ProtocolProto.Request req = ProtocolProto.Request.newBuilder()
//                .setType(ProtocolProto.Request.RequestType.GET_ID)
//                .setAgentie(ProtocolProto.Agentie.newBuilder()
//                        .setId(1)
//                        .setUsername(username)
//                        .setPass(password)
//                        .build())
//                .build();
//        sendRequest(req);
        ProtocolProto.Response response = readResponse();
        return response.getAgentie().getId();
    }
}
