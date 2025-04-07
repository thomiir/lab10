package transportAgency.dto;

import java.io.Serial;
import java.io.Serializable;

public class TripDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;
    private final Long id;
    private final String destination;
    private final java.util.Date departureDate;  // Changed to java.util.Date
    private final java.util.Date departureTime;  // Changed to java.util.Date
    private final Integer noSeatsAvailable;

    public TripDTO(Long id, String destination,
                   java.sql.Date departureDate,
                   java.sql.Time departureTime,
                   Integer noSeatsAvailable) {
        this.id = id;
        this.destination = destination;
        this.departureDate = new java.util.Date(departureDate.getTime());
        this.departureTime = new java.util.Date(departureTime.getTime());
        this.noSeatsAvailable = noSeatsAvailable;
    }

    // Add conversion methods for SQL types if needed
    public java.sql.Date getSqlDepartureDate() {
        return new java.sql.Date(this.departureDate.getTime());
    }

    public java.sql.Time getSqlDepartureTime() {
        return new java.sql.Time(this.departureTime.getTime());
    }

    public Long getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public Integer getNoSeatsAvailable() {
        return noSeatsAvailable;
    }
}