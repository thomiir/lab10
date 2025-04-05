package transportAgency.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class TripDTO implements Serializable {

    private final Long id;
    private final String destination;
    private final Date departureDate;
    private final Time departureTime;
    private final Integer noSeatsAvailable;

    public TripDTO(Long id, String destination, Date departureDate, Time departureTime, Integer noSeatsAvailable) {
        this.id = id;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.noSeatsAvailable = noSeatsAvailable;
    }

    public Long getId() {
        return id;
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
        return "TripDTO[" + id + ' ' + destination + ' ' + departureDate.toString() + ' ' + departureTime.toString() + ']';
    }
}
