package transportAgency.objectprotocol;

import transportAgency.dto.ReservationDTO;

public record MakeReservationResponse(ReservationDTO reservationDTO) implements UpdateResponse {}
