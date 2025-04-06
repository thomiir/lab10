package transportAgency.dto;

import java.io.Serializable;

public record SeatDTO(Integer seatNo, String clientName) implements Serializable {}
