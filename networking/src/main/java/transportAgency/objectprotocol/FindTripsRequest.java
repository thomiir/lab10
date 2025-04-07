package transportAgency.objectprotocol;

import java.io.Serial;

public record FindTripsRequest() implements Request {
    @Serial
    private static final long serialVersionUID = 8L;
}
