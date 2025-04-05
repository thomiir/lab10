package transportAgency.server;

import transportAgency.model.Employee;
import transportAgency.persistence.IEmployeeRepository;
import transportAgency.persistence.IReservationRepository;
import transportAgency.persistence.ITripRepository;
import transportAgency.persistence.jdbc.EmployeeRepository;
import transportAgency.persistence.jdbc.ReservationRepository;
import transportAgency.persistence.jdbc.TripRepository;
import transportAgency.proto.RPCConcurrentServerProto;
import transportAgency.services.IServices;

import java.io.IOException;
import java.util.Properties;

public class RpcServer {
    private static int defaultPort=55555;


    private static void setUp() {
//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                .configure()
//                .build();
    }

    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(RpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }

        setUp();

        IEmployeeRepository agentieRepo=new EmployeeRepository(serverProps);
        ITripRepository tripRepo=new TripRepository(serverProps);
        IReservationRepository rezervareRepo = new ReservationRepository(serverProps, tripRepo);
        IServices serverImpl=new ServicesImpl(agentieRepo, tripRepo, rezervareRepo);
        int ServerPort=defaultPort;
        try {
            ServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+55555);
        System.out.println(agentieRepo.findAll());

        RPCConcurrentServerProto server = new RPCConcurrentServerProto("127.0.0.1",55555, serverImpl);
        try {
            server.start();
        } finally {
//            tearDown();
        }
    }
}