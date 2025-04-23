package tn.esprit.back.Services.Event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Event.Event;
import tn.esprit.back.Entities.Event.PaymentDetails;
import tn.esprit.back.Entities.Event.Reservation;
import tn.esprit.back.Repository.Event.EventRepository;
import tn.esprit.back.Repository.Event.ReservationRepository;

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
    @Autowired
    private QRCodeService qrCodeService;

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

        // Sauvegarder la réservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Mettre à jour le statut de l'événement si nécessaire
        updateEventStatusIfFullyBooked(eventId);

        return savedReservation;
    }

    private void updateEventStatusIfFullyBooked(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Calculer le nombre total de places réservées
        List<Reservation> reservations = reservationRepository.findByEventId(eventId);
        int totalReservedSeats = reservations.stream()
                .mapToInt(Reservation::getNombrePlaces)
                .sum();

        // Si toutes les places sont réservées, mettre à jour le statut
        if (totalReservedSeats >= event.getCapaciteMax()) {
            event.setStatut("Complet");
            eventRepository.save(event);
        }
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
        return reservationRepository.findByEventId(eventId);

    }
    @Override
    public Reservation createReservationWithPayment(Reservation reservation) {
        Event event = eventRepository.findById(reservation.getEvent().getIdEvent())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Vérification des places disponibles
        if(reservation.getSeatNumbers().size() > event.getCapaciteMax()) {
            throw new RuntimeException("Not enough seats available");
        }

        // Vérification des sièges déjà réservés
        List<Reservation> existingReservations = reservationRepository.findByEventId(event.getIdEvent());
        Set<Integer> takenSeats = existingReservations.stream()
                .flatMap(r -> r.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        if(reservation.getSeatNumbers().stream().anyMatch(takenSeats::contains)) {
            throw new RuntimeException("Some seats are already taken");
        }

        // Configuration des dates et montants
        reservation.setDateReservation(new Date());
        reservation.setNombrePlaces(reservation.getSeatNumbers().size());
        reservation.setMontantTotal(event.getPrix() * reservation.getSeatNumbers().size());

        // Gestion du paiement
        if("card".equals(reservation.getPaymentMethod())) {
            reservation.setStatutPaiement("Paid");

            // Validation des détails de paiement
            PaymentDetails paymentDetails = reservation.getPaymentDetails();
            if (paymentDetails == null) {
                throw new RuntimeException("Payment details required for card payment");
            }

            // Lier les détails de paiement à la réservation
            paymentDetails.setReservation(reservation);
        } else {
            reservation.setStatutPaiement("Pending");
            reservation.setPaymentDetails(null); // Pas de détails pour les paiements non-carte
        }
        String qrContent = qrCodeService.generateQRCodeContent(reservation);
        reservation.setQrCodeData(qrContent);
        // Sauvegarder la réservation (cascade sauvegardera aussi paymentDetails)
        Reservation savedReservation = reservationRepository.save(reservation);

        // Mettre à jour le statut de l'événement si nécessaire
        updateEventStatusIfFullyBooked(event.getIdEvent());

        return savedReservation;

    }
    public Reservation getReservationForQRCode(Long id) {
        return reservationRepository.findByIdWithEvent(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
    }

}