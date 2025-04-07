package transportAgency.objectprotocol;

import transportAgency.dto.ReservationDTO;

import java.io.Serial;

public record MakeReservationResponse(ReservationDTO rdto) implements UpdateResponse {
    @Serial
    private static final long serialVersionUID = 13L;
}
