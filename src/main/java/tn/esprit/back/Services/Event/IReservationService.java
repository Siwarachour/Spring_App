package tn.esprit.back.Services.Event;


import tn.esprit.back.Entities.Event.Reservation;

import java.util.List;
import java.util.Optional;

public interface IReservationService {
    Reservation createReservation(Long eventId, List<Integer> seatNumbers);

    Reservation addReservation (Reservation Reservation);
    Reservation updateReservation (Reservation Reservation);
    void deleteReservation (long idReservation);
    List<Reservation> retriveAllReservations ();

    Optional<Reservation> getReservationbyId(long idReservation);

    List<Reservation> findByEventId(Long eventId);

    Reservation createReservationWithPayment(Reservation reservation);
}
