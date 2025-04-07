package transportAgency.model;

import java.io.Serializable;

public class Reservation extends Entity<Long> implements Serializable {
    private final String clientName;
    private final Integer noSeats;
    private final Trip trip;

    public Reservation(Long id, String clientName, Integer noSeats, Trip trip) {
        setId(id);
        this.clientName = clientName;
        this.noSeats = noSeats;
        this.trip = trip;
    }

    public String getClientName() {
        return clientName;
    }

    public Integer getNoSeats() {
        return noSeats;
    }

    public Trip getTrip() {
        return trip;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + getId() + '\'' +
                "clientName='" + clientName + '\'' +
                ", noSeats=" + noSeats +
                ", trip=" + trip +
                '}';
    }
}
