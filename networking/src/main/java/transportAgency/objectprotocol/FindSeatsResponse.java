package transportAgency.objectprotocol;

import transportAgency.dto.SeatDTO;

import java.io.Serial;

public record FindSeatsResponse(SeatDTO[] seats) implements Response {
    @Serial
    private static final long serialVersionUID = 7L;
}
