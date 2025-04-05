package transportAgency.dto;

import transportAgency.model.Trip;

import java.io.Serializable;

public class ReservationDTO implements Serializable {

    private final String clientName;
    private final Integer noSeats;
    private final Trip trip;

    public ReservationDTO(String clientName, Integer noSeats, Trip trip) {
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
    public String toString(){
        return "RezervareDTO["+clientName+' '+ noSeats +' '+ trip+"]";
    }
}