package transportAgency.services;

import transportAgency.model.Employee;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import java.lang.Exception;
import java.sql.Date;
import java.sql.Time;

public interface IServices {
    void login(String username, String password, IObserver client) throws Exception;
    void logout(Employee employee, IObserver client) throws Exception;
    Employee getEmployee(String username, String password) throws Exception;
    Seat[] findAllReservedSeats(String destination, Date date, Time time) throws Exception;
    void makeReservation(String clientName, Integer noSeats, Trip trip) throws Exception;
    Trip[] getAllTrips() throws Exception;
    void addEmployee(Long id, String username, String password);
    Employee getEmployeeByUsername(String username);
    Trip findTripByDestinationDateTime(String destination, Date date, Time time);
}
