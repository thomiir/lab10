package transportAgency.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.persistence.IEmployeeRepository;
import transportAgency.persistence.ITripRepository;
import transportAgency.persistence.IReservationRepository;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServicesImpl implements IServices {
    private static final Logger logger = LogManager.getLogger();

    private final IEmployeeRepository employeeRepository;
    private final ITripRepository tripRepository;
    private final IReservationRepository reservationRepository;
    private final List<IObserver> observers = new CopyOnWriteArrayList<>();

    public ServicesImpl(IEmployeeRepository employeeRepository,
                        ITripRepository tripRepository,
                        IReservationRepository reservationRepository) {
        logger.info("Initializing service with repositories");
        this.employeeRepository = employeeRepository;
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public synchronized Boolean login(String username, String password, IObserver client) {
        logger.trace("Attempting login for user: {}", username);
        Employee employee = employeeRepository.findByUsernameAndPassword(username, password);
        if (employee == null) {
            logger.warn("Login failed for user: {}", username);
            return false;
        }
        addObserver(client);
        logger.info("User {} logged in successfully", username);
        return true;
    }

    @Override
    public Employee getEmployee(String username, String password) {
        logger.trace("Retrieving employee: {}", username);
        return employeeRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public List<Seat> findAllReservedSeats(String destination, String date, String time) {
        logger.traceEntry("Finding reserved seats for trip: {} on {} at {}", destination, date, time);
        try {
            Date d = Date.valueOf(date);
            Time t = Time.valueOf(time);
            Trip trip = tripRepository.findTripByDestinationDateTime(destination, d, t);

            if (trip == null) {
                logger.warn("No trip found for destination: {} on {} at {}", destination, date, time);
                return new ArrayList<>();
            }

            List<Seat> seats = new ArrayList<>();
            List<Reservation> reservations = reservationRepository.findAllReservationsForTrip(trip);
            long seatNumber = 1L;

            // Add reserved seats
            for (Reservation reservation : reservations) {
                for (int i = 0; i < reservation.getNoSeats(); i++) {
                    seats.add(new Seat(seatNumber++, reservation.getClientName()));
                }
            }

            // Add available seats
            while (seatNumber <= 18) {
                seats.add(new Seat(seatNumber++, "-"));
            }

            logger.traceExit();
            return seats;
        } catch (Exception e) {
            logger.error("Error finding reserved seats", e);
            throw new RuntimeException("Error finding reserved seats", e);
        }
    }

    @Override
    public synchronized void logout(Employee employee, IObserver client) {
        logger.trace("Logging out user: {}", employee.getUsername());
        removeObserver(client);
        logger.info("User {} logged out successfully", employee.getUsername());
    }

    @Override
    public void makeReservation(String clientName, Integer noSeats, Long tripId) throws Exception {
        Trip trip = tripRepository.findOne(tripId);
        if (trip == null) throw new Exception("Trip not found");

        if (noSeats > trip.getNoSeatsAvailable()) {
            throw new Exception("Not enough seats available");
        }

        Reservation reservation = new Reservation(null, clientName, noSeats, trip);
        reservationRepository.save(reservation);

        Trip updatedTrip = new Trip(tripId, trip.getDestination(),
                trip.getDepartureDate(), trip.getDepartureTime(),
                trip.getNoSeatsAvailable() - noSeats);
        tripRepository.update(tripId, updatedTrip);

        notifyObservers();
    }


    @Override
    public List<Trip> getAllTrips() {
        logger.trace("Retrieving all trips");
        try {
            return (List<Trip>) tripRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving trips", e);
            throw new RuntimeException("Error retrieving trips", e);
        }
    }

    @Override
    public long getId(String username, String password) throws Exception {
        Employee employee = getEmployee(username, password);
        if (employee != null) {
            return employee.getId();
        }
        throw new Exception("Employee not found");
    }

    @Override
    public Trip findTrip(String destination, Date departureDate, Time departureTime) throws Exception {
        try {
            return tripRepository.findTripByDestinationDateTime(destination, departureDate, departureTime);
        } catch (Exception e) {
            throw new Exception("Error finding trip", e);
        }
    }

    @Override
    public Trip findTripById(Long id) {
        return tripRepository.findOne(id);
    }

    @Override
    public synchronized void addObserver(IObserver observer) {
    }

    @Override
    public synchronized void removeObserver(IObserver observer) {
    }

    public void notifyObservers() {

    }

    public boolean tripExists(String destination, String date, String time) {
        try {
            return tripRepository.findTripByDestinationDateTime(
                    destination,
                    Date.valueOf(date),
                    Time.valueOf(time)
            ) != null;
        } catch (Exception e) {
            logger.error("Error checking trip existence", e);
            return false;
        }
    }
}