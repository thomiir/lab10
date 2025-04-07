package transportAgency.objectprotocol;


import transportAgency.dto.TripDTO;

import java.io.Serial;

public record FindTripsResponse(TripDTO[] trips) implements Response {
    @Serial
    private static final long serialVersionUID = 9L;
}
