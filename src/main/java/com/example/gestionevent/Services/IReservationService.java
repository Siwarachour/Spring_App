package com.example.gestionevent.Services;

import com.example.gestionevent.entities.Reservation;

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
}
