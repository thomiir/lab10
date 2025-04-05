package transportAgency.persistence;

import transportAgency.model.Reservation;
import transportAgency.model.Trip;

import java.util.List;

public interface IReservationRepository extends IRepository<Long, Reservation> {

    List<Reservation> findAllReservationsForTrip(Trip trip);
}
