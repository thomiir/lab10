package transportAgency.persistence.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.model.Reservation;
import transportAgency.model.Trip;
import transportAgency.persistence.interfaces.IReservationRepository;
import transportAgency.persistence.interfaces.ITripRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ReservationRepository implements IReservationRepository {

    private static JDBCUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    private final ITripRepository tripRepository;

    public ReservationRepository(Properties properties, ITripRepository tripRepository) {
        this.tripRepository = tripRepository;
        logger.info("Init ReservationRepository, properties: {}", properties);
        dbUtils = new JDBCUtils(properties);
    }

    @Override
    public Reservation findOne(Long id)  {
        logger.traceEntry("Find reservation {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from reservations where id = ?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Reservation reservation = createReservation(result);
                    logger.traceExit("Reservation {} found", id);
                    return reservation;
                }
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit("Reservation {} not found", id);
        return null;
    }

    private Reservation createReservation(ResultSet result) throws SQLException {
        Long result_id = result.getLong("id");
        String clientName = result.getString("clientName");
        Integer noSeats = result.getInt("noSeats");
        long tripId = result.getLong("trip");
        Trip trip = tripRepository.findOne(tripId);
        return new Reservation(result_id, clientName, noSeats, trip);
    }

    @Override
    public Iterable<Reservation> findAll() {
        logger.traceEntry("Find all reservations");
        List<Reservation> reservationList = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from reservations")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next())
                    reservationList.add(createReservation(result));
            } catch (Exception e) {
                logger.trace(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return reservationList;
    }

    @Override
    public Reservation save(Reservation entity) {
        logger.traceEntry("Save reservation {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into reservations (clientName, noSeats, trip) values (?,?,?)")) {
            preStmt.setString(1, entity.getClientName());
            preStmt.setInt(2, entity.getNoSeats());
            preStmt.setLong(3, entity.getTrip().getId());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} reservations", result);
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit();
        return entity;
    }

    @Override
    public void delete(Long id) {
        logger.traceEntry("Delete reservation {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from reservations where id=?")) {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} reservations", result);
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public Reservation update(Long id, Reservation entity) {
        logger.traceEntry("Update reservation {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("update reservations set clientName = ?, noSeats = ?, trip = ? where id = ?")) {
            preparedStatement.setString(1, entity.getClientName());
            preparedStatement.setInt(2, entity.getNoSeats());
            preparedStatement.setLong(3, entity.getTrip().getId());
            preparedStatement.setLong(4, id);
            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} reservations", result);
            entity.setId(id);
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit();
        return entity;
    }

    @Override
    public List<Reservation> findAllReservationsForTrip(Trip trip) {
        logger.traceEntry("Find all reservations for trip {}", trip);
        List<Reservation> reservationList = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from reservations where trip=?")) {
            preStmt.setLong(1, trip.getId());
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next())
                    reservationList.add(createReservation(result));
            } catch (Exception e) {
                logger.trace(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return reservationList;
    }
}
