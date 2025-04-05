import transportAgency.model.Employee;
import transportAgency.model.ReservationDTO;
import transportAgency.model.Trip;

import java.util.List;

public interface IServices {
    Boolean login(String username, String password);
    Employee getEmployee(String username, String password);
    List<ReservationDTO> findAllReservedSeats(String destination, String date, String time);
    void makeReservation(String clientName, Integer noSeats, Long tripId);
    List<Trip> getAllTrips();
    Boolean tripExists(String destination, String date, String time);
}
