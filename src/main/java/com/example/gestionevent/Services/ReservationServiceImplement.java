package com.example.gestionevent.Services;

import com.example.gestionevent.Repository.EventRepository;
import com.example.gestionevent.Repository.ReservationRepository;
import com.example.gestionevent.entities.Event;
import com.example.gestionevent.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImplement implements IReservationService {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    private EventRepository eventRepository;

    @Override
    public Reservation createReservation(Long eventId, List<Integer> seatNumbers) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Vérifier la disponibilité des places
        if(seatNumbers.size() > event.getCapaciteMax()) {
            throw new RuntimeException("Not enough seats available");
        }

        // Vérifier si les places sont déjà réservées
        List<Reservation> existingReservations = reservationRepository.findByEventId(eventId);
        Set<Integer> takenSeats = existingReservations.stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        if(seatNumbers.stream().anyMatch(takenSeats::contains)) {
            throw new RuntimeException("Some seats are already taken");
        }

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setSeatNumbers(seatNumbers);
        reservation.setNombrePlaces(seatNumbers.size());
        reservation.setMontantTotal(event.getPrix() * seatNumbers.size());
        reservation.setDateReservation(new Date());
        reservation.setStatutPaiement("Pending");

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation addReservation(Reservation Reservation) {
        return reservationRepository.save(Reservation);
    }

    @Override
    public Reservation updateReservation(Reservation Reservation) {
        return reservationRepository.save(Reservation);
    }

    @Override
    public void deleteReservation(long idReservation) {
        reservationRepository.deleteById(idReservation);
    }

    @Override
    public List<Reservation> retriveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> getReservationbyId(long idReservation) {
        return reservationRepository.findById(idReservation); // Now returns Optional
    }

    @Override
    public List<Reservation> findByEventId(Long eventId) {
        return null;
    }
}