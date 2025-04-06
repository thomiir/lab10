package transportAgency.server;

import transportAgency.persistence.IEmployeeRepository;
import transportAgency.persistence.IReservationRepository;
import transportAgency.persistence.ITripRepository;
import transportAgency.persistence.jdbc.EmployeeRepository;
import transportAgency.persistence.jdbc.ReservationRepository;
import transportAgency.persistence.jdbc.TripRepository;
import transportAgency.services.IServices;
import transportAgency.utils.AbstractServer;
import transportAgency.utils.ChatObjectConcurrentServer;

import java.io.IOException;
import java.util.Properties;

public class Server {
    private static int defaultPort=55555;


    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(Server.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }

        IEmployeeRepository emplRepo=new EmployeeRepository(serverProps);
        ITripRepository tripRepo=new TripRepository(serverProps);
        IReservationRepository reservationRepo = new ReservationRepository(serverProps, tripRepo);
        IServices serverImpl=new ServicesImpl(emplRepo, tripRepo, reservationRepo);
        int ServerPort=defaultPort;
        try {
            ServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+55555);

        AbstractServer server = new ChatObjectConcurrentServer(ServerPort,serverImpl);
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting server "+e);
            System.err.println(e.getStackTrace());
        }
    }
}