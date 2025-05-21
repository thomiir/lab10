package transportAgency.dto;

import java.sql.Date;
import java.sql.Time;
import transportAgency.model.*;

public class DTOUtils {
    public static Employee getFromDTO(EmployeeDTO employeeDTO){
        String username = employeeDTO.getUsername();
        String password = employeeDTO.getPassword();
        return new Employee(-1L, username, password);
    }
    public static EmployeeDTO getDTO(Employee employee){
        String username = employee.getUsername();
        String password = employee.getPassword();
        return new EmployeeDTO(username,password);
    }

    public static Trip getFromDTO(TripDTO tripDTO){
        Long id = tripDTO.getId();
        String destination = tripDTO.getDestination();
        Date departureDate = tripDTO.getSqlDepartureDate();
        Time departureTime = tripDTO.getSqlDepartureTime();
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
        try {
            Seat[] seats = new Seat[18];
            for (int i = 0; i < 18; i++) {
                seats[i] = new Seat(seatDTOs[i].seatNo(), seatDTOs[i].clientName());
            }
            return seats;
        }
        catch (Exception e) {
            System.out.println("Error: Array index out of bounds. Please check the input data.");
            return new Seat[0]; // Return an empty array or handle the error as needed
        }
    }

    public static SeatDTO[] getDTO(Seat[] seats) {
        try {
            SeatDTO[] seatDTOs = new SeatDTO[18];
            for (int i = 0; i < 18; i++) {
                seatDTOs[i] = new SeatDTO(seats[i].seatNo(), seats[i].clientName());
            }
            return seatDTOs;
        }
        catch (Exception e) {
            System.out.println("Error: Array index out of bounds. Please check the input data.");
            return new SeatDTO[0];
        }
    }

    public static TripDTO[] getDTO(Trip[] trips) {
        TripDTO[] tripDTOs = new TripDTO[trips.length];
        for (int i = 0; i < trips.length; i++) {
            tripDTOs[i] = getDTO(trips[i]);
        }
        return tripDTOs;
    }
}