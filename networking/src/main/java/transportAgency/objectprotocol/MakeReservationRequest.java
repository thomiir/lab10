package transportAgency.objectprotocol;

import transportAgency.dto.ReservationDTO;

import java.io.Serial;

public record MakeReservationRequest(ReservationDTO reservation) implements Request {
    @Serial
    private static final long serialVersionUID = 12L;
}
