package transportAgency.objectprotocol;

import transportAgency.dto.SeatDTO;

public record FindSeatsResponse(SeatDTO[] seats) implements Response {}
