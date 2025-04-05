package transportAgency.proto;

import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.net.Socket;

public class RPCConcurrentServerProto extends ConcurrentServerProto {
    private IServices server;
    private ProjectProtoWorker worker;

    public RPCConcurrentServerProto(String host, int port, IServices server) {
        super(host, port);
        this.server = server;
        System.out.println("RPCConcurrentServer...");
    }

    @Override
    protected Thread createWorker(Socket client) {
        try
        {
            worker = new ProjectProtoWorker(server, client);
        }
        catch (Exception e)
        {
            System.out.println("Error creating worker: " + e.getMessage());
            return null;
        }
        return new Thread(worker::run);
    }
}
