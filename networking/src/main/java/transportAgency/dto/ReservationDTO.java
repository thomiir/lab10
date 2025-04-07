package transportAgency.dto;

import java.io.Serial;
import java.io.Serializable;

public record ReservationDTO(String clientName, Integer noSeats, TripDTO trip) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    @Override
    public String toString() {
        return "RezervareDTO[" + clientName + ' ' + noSeats + ' ' + trip + "]";
    }
}