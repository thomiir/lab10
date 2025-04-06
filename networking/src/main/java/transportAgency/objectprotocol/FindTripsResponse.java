package transportAgency.objectprotocol;


import transportAgency.dto.TripDTO;

public record FindTripsResponse(TripDTO[] trips) implements Response {}
