package transportAgency.model;

import java.io.Serializable;

public record Seat(Integer seatNo, String clientName) implements Serializable {}
