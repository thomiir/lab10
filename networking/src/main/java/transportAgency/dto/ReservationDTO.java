package transportAgency.dto;

import java.io.Serializable;

public record ReservationDTO(String clientName, Integer noSeats, TripDTO trip) implements Serializable {

    @Override
    public String toString() {
        return "RezervareDTO[" + clientName + ' ' + noSeats + ' ' + trip + "]";
    }
}