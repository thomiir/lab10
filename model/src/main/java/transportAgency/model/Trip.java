package example.domain;

import java.sql.Date;
import java.sql.Time;

public class Trip extends Entity<Long>{
    private final String destination;
    private final Date departureDate;
    private final Time departureTime;
    private final Integer noSeatsAvailable;

    public Trip(Long id, String destination, Date departureDate, Time departureTime, Integer noSeatsAvailable) {
        setId(id);
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.noSeatsAvailable = noSeatsAvailable;
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
