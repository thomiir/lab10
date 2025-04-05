package transportAgency.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class SearchDTO implements Serializable{
    private String destination;
    private Date departureDate;
    private Time departureTime;

    public SearchDTO(String destination, Date departureDate, Time departureTime) {
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
    }

    @Override
    public String toString(){
        return "SearchDTO["+destination+","+departureDate.toString()+","+departureTime.toString()+"]";
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
}