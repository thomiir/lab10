package transportAgency.objectprotocol;

import transportAgency.dto.TripDTO;

import java.io.Serial;

public record FindSeatsRequest(TripDTO trip) implements Request {
    @Serial
    private static final long serialVersionUID = 6L;
}
