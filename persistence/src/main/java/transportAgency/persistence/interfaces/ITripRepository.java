package transportAgency.persistence.interfaces;

import transportAgency.model.Trip;

import java.sql.Date;
import java.sql.Time;

public interface ITripRepository extends IRepository<Long, Trip> {

    Trip findTripByDestinationDateTime(String destination, Date date, Time time);
}
