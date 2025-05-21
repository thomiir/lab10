package transportAgency.rest.client;

public class TransportAgencyException extends Exception {
    public TransportAgencyException() {
    }

    public TransportAgencyException(String message) {
        super(message);
    }

    public TransportAgencyException(String message, Throwable cause) {
        super(message, cause);
    }
}