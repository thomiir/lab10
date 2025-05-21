package transportAgency.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.persistence.interfaces.IEmployeeRepository;
import transportAgency.persistence.interfaces.ITripRepository;
import transportAgency.persistence.interfaces.IReservationRepository;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServicesImpl implements IServices {
    private static final Logger logger = LogManager.getLogger();

    private final IEmployeeRepository employeeRepository;
    private final ITripRepository tripRepository;
    private final IReservationRepository reservationRepository;
    private Map<Long, IObserver> loggedClients = new ConcurrentHashMap<>();

    public ServicesImpl(IEmployeeRepository employeeRepository,
                        ITripRepository tripRepository,
                        IReservationRepository reservationRepository) {
        logger.info("Initializing service with repositories");
        this.employeeRepository = employeeRepository;
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
    }

    public void addEmployee(Long id, String username, String password) {
//        employeeRepository.save(new Employee(id, username, new BCryptPasswordEncoder().encode(password)));
    }

    @Override
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public Trip findTripByDestinationDateTime(String destination, Date date, Time time) {
        return tripRepository.findTripByDestinationDateTime(destination, date, time);
    }

    @Override
    public synchronized void login(String username, String password, IObserver client) throws Exception {
        logger.trace("Attempting login for user: {}", username);
        Employee employee = employeeRepository.findByUsernameAndPassword(username, password);
        if (employee == null) {
            logger.warn("Login failed for user: {}", username);
            throw new Exception("Login failed.");
        }
        if (loggedClients.containsKey(employee.getId())) {
            logger.warn("User {} is already logged in", username);
            throw new Exception("User already logged in.");
        }
        loggedClients.put(employee.getId(), client);
        System.out.println("loggedclients size: " + loggedClients.size());
        logger.info("User {} logged in successfully", username);
    }

    @Override
    public synchronized Employee getEmployee(String username, String password) {
        logger.trace("Retrieving employee: {}", username);
        return employeeRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public synchronized Seat[] findAllReservedSeats(String destination, Date date, Time time) {
        logger.traceEntry("Finding reserved seats for trip: {} on {} at {}", destination, date, time);
        try {
            Trip trip = tripRepository.findTripByDestinationDateTime(destination, date, time);
            if (trip == null) {
                logger.warn("No trip found for destination: {} on {} at {}", destination, date, time);
                return new Seat[0];
            }

            Seat[] seats = new Seat[18];
            List<Reservation> reservations = reservationRepository.findAllReservationsForTrip(trip);
            int seatNumber = 0;

            for (Reservation reservation : reservations) {
                for (int i = 1; i <= reservation.getNoSeats(); i++) {
                    seats[seatNumber] = new Seat(seatNumber, reservation.getClientName());
                    seatNumber++;
                }
            }

            while (seatNumber <= 17) {
                seats[seatNumber] = new Seat(seatNumber, "-");
                seatNumber++;
            }
            System.out.println(Arrays.toString(seats));
            logger.traceExit();
            return seats;
        } catch (Exception e) {
            logger.error("Error finding reserved seats", e);
            throw new RuntimeException("Error finding reserved seats", e);
        }
    }

    @Override
    public synchronized void logout(Employee employee, IObserver client) {
        loggedClients.remove(employee.getId());
        logger.trace("Logging out user: {}", employee.getUsername());
        logger.info("User {} logged out successfully", employee.getUsername());
    }

    @Override
    public synchronized void makeReservation(String clientName, Integer noSeats, Trip trip) throws Exception {
        if (trip == null)
            throw new Exception("Trip not found");
        Trip tripObject = findTripByDestinationDateTime(trip.getDestination(), trip.getDepartureDate(), trip.getDepartureTime());
        if (noSeats > tripObject.getNoSeatsAvailable()) {
            throw new Exception("Not enough seats available");
        }
        Reservation reservation = new Reservation(null, clientName, noSeats, tripObject);
        reservationRepository.save(reservation);
        Trip updatedTrip = new Trip(tripObject.getId(),
                trip.getDestination(),
                trip.getDepartureDate(),
                trip.getDepartureTime(),
                tripObject.getNoSeatsAvailable() - noSeats);
        tripRepository.update(tripObject.getId(), updatedTrip);
        new Thread(() -> {
            try {
                for (IObserver client : loggedClients.values()) {
                    try {
                        client.reservationMade(reservation);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Error notifying client about trip update", e);
                    }
                }
            } catch (Exception e) {
                logger.error("Error notifying clients about reservation", e);
            }
        }).start();

    }

    @Override
    public synchronized Trip[] getAllTrips() {
        logger.trace("Retrieving all trips");
        try {
            Iterable<Trip> tripIterable = tripRepository.findAll();
            List<Trip> tripList = new ArrayList<>();
            tripIterable.forEach(tripList::add);
            return tripList.toArray(new Trip[0]);
        } catch (Exception e) {
            logger.error("Error retrieving trips", e);
            throw new RuntimeException("Error retrieving trips", e);
        }
    }
}