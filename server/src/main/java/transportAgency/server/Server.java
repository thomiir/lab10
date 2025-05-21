package transportAgency.server;

import transportAgency.persistence.hibernate.EmployeeRepository;
import transportAgency.persistence.interfaces.IReservationRepository;
import transportAgency.persistence.interfaces.ITripRepository;
import transportAgency.persistence.interfaces.IEmployeeRepository;
import transportAgency.persistence.hibernate.ReservationRepository;
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
            serverProps.load(Server.class.getResourceAsStream("/hibernate.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find hibernate.properties "+e);
            return;
        }

        IEmployeeRepository emplRepo=new EmployeeRepository();
        ITripRepository tripRepo=new TripRepository(serverProps);
        IReservationRepository reservationRepo = new ReservationRepository();
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
//        AbstractServer server = new ProtoConcurrentServer(ServerPort, serverImpl);
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting server "+e);
            System.err.println(e.getStackTrace());
        }
    }
}