package transportAgency.objectprotocol;

import transportAgency.dto.ReservationDTO;

public record MakeReservationRequest(ReservationDTO reservation) implements Request {}
