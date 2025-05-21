package transportAgency.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name="reservations")
public class Reservation extends Entity<Long> {
    private String clientName;
    private Integer noSeats;

    @ManyToOne
    @JoinColumn(name="trip")
    private Trip trip;

    public Reservation(Long id, String clientName, Integer noSeats, Trip trip) {
        setId(id);
        this.clientName = clientName;
        this.noSeats = noSeats;
        this.trip = trip;
    }

    public Reservation() {

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
