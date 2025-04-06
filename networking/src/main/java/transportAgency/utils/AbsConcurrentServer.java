package transportAgency.utils;

import java.net.Socket;

public abstract class AbsConcurrentServer extends AbstractServer {
//    private static Logger logger = LogManager.getLogger(AbsConcurrentServer.class);
    public AbsConcurrentServer(int port) {
        super(port);
//        logger.debug("Concurrent AbstractServer");
    }

    protected void processRequest(Socket client) {
        Thread tw=createWorker(client);
        tw.start();
    }

    protected abstract Thread createWorker(Socket client) ;


}
