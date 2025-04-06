package transportAgency.utils;

import transportAgency.objectprotocol.ClientWorker;
import transportAgency.services.IServices;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.net.Socket;


public class ChatObjectConcurrentServer extends AbsConcurrentServer {

    private IServices chatServer;

//    private static Logger logger = LogManager.getLogger(ChatJsonConcurrentServer.class);

    public ChatObjectConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
//        logger.info("Chat-ChatObjectConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientWorker worker=new ClientWorker(chatServer, client);
        Thread tw=new Thread(worker);
        return tw;
    }
}

