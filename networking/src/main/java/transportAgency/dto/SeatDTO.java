package transportAgency.dto;

import java.io.Serial;
import java.io.Serializable;

public record SeatDTO(Integer seatNo, String clientName) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
}
