package transportAgency.services;

import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import java.lang.Exception;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface IServices {
    Boolean login(String username, String password, IObserver client) throws Exception;
    void logout(Employee employee, IObserver client) throws Exception;
    Employee getEmployee(String username, String password) throws Exception;
    List<Seat> findAllReservedSeats(String destination, String date, String time) throws Exception;
    void makeReservation(String clientName, Integer noSeats, Long tripId) throws Exception;
    List<Trip> getAllTrips() throws Exception;
//    Boolean tripExists(String destination, String date, String time) throws Exception;
    public long getId(String username, String password) throws Exception;
    Trip findTrip(String destination, Date departureDate, Time departureTime) throws Exception;
    Trip findTripById(Long id) throws Exception;
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers();
}
