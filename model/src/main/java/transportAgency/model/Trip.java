package transportAgency.model;

import jakarta.persistence.Table;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@jakarta.persistence.Entity
@Table(name="trips")
public class Trip extends Entity<Long> {
    private String destination;
    private Date departureDate;
    private Time departureTime;
    private Integer noSeatsAvailable;

    public Trip(Long id, String destination, Date departureDate, Time departureTime, Integer noSeatsAvailable) {
        setId(id);
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.noSeatsAvailable = noSeatsAvailable;
    }

    public Trip(String destination, Date departureDate, Time departureTime, Integer noSeatsAvailable) {
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.noSeatsAvailable = noSeatsAvailable;
    }

    public Trip() {

    }

    public String getDestination() {
        return destination;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public Integer getNoSeatsAvailable() {
        return noSeatsAvailable;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + getId() + '\'' +
                "destination='" + destination + '\'' +
                ", departureDate=" + departureDate +
                ", departureTime=" + departureTime +
                ", noSeatsAvailable=" + noSeatsAvailable +
                '}';
    }
}
