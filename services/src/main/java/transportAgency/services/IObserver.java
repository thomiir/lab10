package transportAgency.services;

import transportAgency.model.Reservation;

public interface IObserver {
    void reservationMade(Reservation reservation) throws TransportAgencyException;
}