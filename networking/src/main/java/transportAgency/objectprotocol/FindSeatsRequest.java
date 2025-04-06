package transportAgency.objectprotocol;

import transportAgency.dto.TripDTO;

public record FindSeatsRequest(TripDTO trip) implements Request {}
