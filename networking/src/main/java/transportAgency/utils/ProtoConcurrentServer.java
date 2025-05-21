package transportAgency.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.objectprotocol.ClientWorker;
import transportAgency.protobufprotocol.ProtoClientWorker;
import transportAgency.services.IServices;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.net.Socket;


public class ProtoConcurrentServer extends AbsConcurrentServer {

    private final IServices chatServer;

    private static final Logger logger = LogManager.getLogger(ProtoConcurrentServer.class);

    public ProtoConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        logger.info("ProtoConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        return new Thread(new ProtoClientWorker(chatServer, client));
    }
}

