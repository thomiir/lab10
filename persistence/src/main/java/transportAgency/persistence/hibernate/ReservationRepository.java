package transportAgency.persistence.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import transportAgency.model.Reservation;
import transportAgency.model.Trip;
import transportAgency.persistence.interfaces.IReservationRepository;

import java.util.List;

public class ReservationRepository implements IReservationRepository {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<Reservation> findAllReservationsForTrip(Trip trip) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Query<Reservation> query = session.createQuery("from Reservation where trip=:tripM", Reservation.class);
            query.setParameter("tripM", trip);
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public Reservation findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Reservation> findAll() {
        return null;
    }

    @Override
    public Reservation save(Reservation entity) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return entity;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Reservation update(Long aLong, Reservation entity) {
        return new Reservation();
    }
}
