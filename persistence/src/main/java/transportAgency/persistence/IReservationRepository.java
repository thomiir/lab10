package example.repository;

import example.domain.Reservation;
import example.domain.Trip;

import java.util.List;

public interface IReservationRepository extends IRepository<Long, Reservation> {

    List<Reservation> findAllReservationsForTrip(Trip trip);
}
