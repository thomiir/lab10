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
        Trip trip = reservation.getTrip();
        return new ReservationDTO(clientName, noSeats, trip);
    }

    public static Reservation getFromDTO(ReservationDTO reservationDTO) {
        String clientName = reservationDTO.getClientName();
        Integer noSeats = reservationDTO.getNoSeats();
        Trip trip = reservationDTO.getTrip();
        return new Reservation(-1L, clientName, noSeats, trip);
    }

    public static EmployeeDTO[] getDTO(Employee[] agenties){
        EmployeeDTO[] frDTO=new EmployeeDTO[agenties.length];
        for(int i=0;i<agenties.length;i++)
            frDTO[i]=getDTO(agenties[i]);
        return frDTO;
    }

    public static Employee[] getFromDTO(EmployeeDTO[] agentieDTOS){
        Employee[] friends=new Employee[agentieDTOS.length];
        for(int i=0;i<agentieDTOS.length;i++){
            friends[i]=getFromDTO(agentieDTOS[i]);
        }
        return friends;
    }
}