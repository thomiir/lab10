package transportAgency.dto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import transportAgency.model.*;

public class DTOUtils {
    public static Employee getFromDTO(EmployeeDTO employeeDTO){
        String username = employeeDTO.getUsername();
        return new Employee(-1L, username, "");
    }
    public static EmployeeDTO getDTO(Employee employee){
        String username = employee.getUsername();
        return new EmployeeDTO(username,"");
    }

    public static Trip getFromDTO(TripDTO tripDTO){
        Long id = tripDTO.getId();
        String destination = tripDTO.getDestination();
        Date departureDate = tripDTO.getDepartureDate();
        Time departureTime = tripDTO.getDepartureTime();
        Integer noSeatsAvailable = tripDTO.getNoSeatsAvailable();
        return new Trip(id, destination, departureDate, departureTime, noSeatsAvailable);
    }

    public static TripDTO getDTO(Trip trip){
        Long id = trip.getId();
        String destination = trip.getDestination();
        Date departureDate = trip.getDepartureDate();
        Time departureTime = trip.getDepartureTime();
        Integer noSeatsAvailable = trip.getNoSeatsAvailable();
        return new TripDTO(id, destination, departureDate, departureTime, noSeatsAvailable);
    }

    public static ReservationDTO getDTO(Reservation reservation){
        String clientName = reservation.getClientName();
        Integer noSeats = reservation.getNoSeats();
        TripDTO trip = getDTO(reservation.getTrip());
        return new ReservationDTO(clientName, noSeats, trip);
    }

    public static Reservation getFromDTO(ReservationDTO reservationDTO) {
        String clientName = reservationDTO.clientName();
        Integer noSeats = reservationDTO.noSeats();
        Trip trip = getFromDTO(reservationDTO.trip());
        return new Reservation(-1L, clientName, noSeats, trip);
    }

    public static Trip[] getFromDTO(TripDTO[] tripDTOs) {
        Trip[] trips = new Trip[tripDTOs.length];
        for (int i = 0; i < tripDTOs.length; i++) {
            trips[i] = getFromDTO(tripDTOs[i]);
        }
        return trips;
    }

    public static Seat[] getFromDTO(SeatDTO[] seatDTOs) {
        Seat[] seats = new Seat[seatDTOs.length];
        for (int i = 0; i < 18; i++) {
            seats[i] = new Seat(seatDTOs[i].seatNo(), seatDTOs[i].clientName());
        }
        return seats;
    }

    public static SeatDTO[] getDTO(Seat[] seats) {
        SeatDTO[] seatDTOs = new SeatDTO[seats.length];
        for (int i = 0; i < 18; i++) {
            seatDTOs[i] = new SeatDTO(seats[i].seatNo(), seats[i].clientName());
        }
        return seatDTOs;
    }

    public static TripDTO[] getDTO(Trip[] trips) {
        TripDTO[] tripDTOs = new TripDTO[trips.length];
        for (int i = 0; i < trips.length; i++) {
            tripDTOs[i] = getDTO(trips[i]);
        }
        return tripDTOs;
    }
}