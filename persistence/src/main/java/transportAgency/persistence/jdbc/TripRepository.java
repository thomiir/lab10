package transportAgency.persistence.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import transportAgency.model.Trip;
import transportAgency.persistence.interfaces.ITripRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class TripRepository implements ITripRepository {

    private static JDBCUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public TripRepository(Properties properties) {
        logger.info("Init TripRepository, properties:{}", properties);
        dbUtils = new JDBCUtils(properties);
    }

    @Override
    public Trip findTripByDestinationDateTime(String destination, Date date, Time time) {
        logger.traceEntry("Find trip destination {} date {} time {}", destination, date, time);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from trips where destination = ? and departureDate = ? and departureTime = ?")) {
            preStmt.setString(1, destination);
            preStmt.setDate(2, date);
            preStmt.setTime(3, time);
            try (ResultSet result = preStmt.executeQuery()) {
                result.next();
                logger.trace("found trip");
                System.out.println(result.getLong(1));
                return createTrip(result);
            }
        } catch (SQLException ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public Trip findOne(Long id)  {
        logger.traceEntry("Find trip {} ", id);
        System.out.println("find trip" + id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from trips where id = ?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Trip trip = createTrip(result);
                    logger.traceExit("Trip {} found", id);
                    return trip;
                }
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit("Trip {} not found", id);
        return null;
    }

    private static Trip createTrip(ResultSet result) throws SQLException {
        Long id = result.getLong(1);
        String destination = result.getString(2);
        Date date = result.getDate(3);
        Time time = result.getTime(4);
        Integer seats = result.getInt(5);
        return new Trip(id, destination, date, time, seats);
    }

    @Override
    public Iterable<Trip> findAll() {
        logger.traceEntry("Find all trips");
        List<Trip> tripList = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from trips")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next())
                    tripList.add(createTrip(result));
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
        return tripList;
    }

    @Override
    public Trip save(Trip entity) {
        logger.traceEntry("Save trip {} ", entity.getId());
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into trips (destination, departureDate, departureTime, noSeatsAvailable) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setString(1, entity.getDestination());
            preStmt.setDate(2, entity.getDepartureDate());
            preStmt.setTime(3, entity.getDepartureTime());
            preStmt.setInt(4, entity.getNoSeatsAvailable());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} trips", result);
            if (result > 0) {
                ResultSet rs = preStmt.getGeneratedKeys();
                if (rs.next())
                    entity.setId(rs.getLong(1));
            }
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
        logger.traceEntry("Delete trip {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from trips where id=?")) {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} trips", result);
        } catch (SQLException ex) {
            logger.error(ex);
        }
        logger.traceExit();
    }

    @Override
    public Trip update(Long id, Trip entity) {
        logger.traceEntry("Update trip {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("update trips set destination = ?, departureDate = ?, departureTime = ?, noSeatsAvailable = ? where id = ?")) {
            preparedStatement.setString(1, entity.getDestination());
            preparedStatement.setDate(2, entity.getDepartureDate());
            preparedStatement.setTime(3, entity.getDepartureTime());
            preparedStatement.setInt(4, entity.getNoSeatsAvailable());
            preparedStatement.setLong(5, id);
            int result = preparedStatement.executeUpdate();
            entity.setId(id);
            logger.trace("Updated {} trips", result);
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit();
        return entity;
    }
}
