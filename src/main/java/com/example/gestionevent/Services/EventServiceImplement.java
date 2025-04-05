package com.example.gestionevent.Services;

import com.example.gestionevent.Repository.EventRepository;
import com.example.gestionevent.Repository.ReservationRepository;
import com.example.gestionevent.Repository.ReviewRepository;
import com.example.gestionevent.Repository.SponsorRepository;
import com.example.gestionevent.entities.Event;
import com.example.gestionevent.entities.Reservation;
import com.example.gestionevent.entities.Review;
import com.example.gestionevent.entities.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImplement implements IEventService{
    @Autowired
    EventRepository eventRepository;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    SponsorRepository sponsorRepository;
    @Autowired
    ReviewRepository reviewRepository;


    @Override
    public Event updateEvent(Long idEvent, Event eventDetails) {
        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Event not found"));


        event.setNomEvent(eventDetails.getNomEvent());
        event.setDescriptionEvent(eventDetails.getDescriptionEvent());
        event.setDateDebut(eventDetails.getDateDebut());
        event.setDateFin(eventDetails.getDateFin());
        event.setLieu(eventDetails.getLieu());
        event.setCapaciteMax(eventDetails.getCapaciteMax());
        event.setPrix(eventDetails.getPrix());
        event.setStatut(eventDetails.getStatut());
        event.setTypeEvenement(eventDetails.getTypeEvenement());


        // Ne pas écraser l'image si elle n'est pas fournie dans eventDetails
        if (eventDetails.getImageEvent() != null) {
            event.setImageEvent(eventDetails.getImageEvent());
        }

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(long idEvent) {
        eventRepository.deleteById(idEvent);

    }

    @Override
    public List<Event> retriveAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventbyId(long idEvent) {
        return eventRepository.findById(idEvent).get();
    }

    @Override
    public Event effectereservationToEvent(List<Long> idReservation, long idEvent) {
        List<Reservation> reservations = reservationRepository.findAllById(idReservation);
        Event event = eventRepository.findById(idEvent).orElseThrow(() -> new RuntimeException("Event not found"));

        int totalPlaces = reservations.stream().mapToInt(Reservation::getNombrePlaces).sum();

        // Vérifier si l'événement a suffisamment de places disponibles
        if (event.getCapaciteMax() < totalPlaces) {
            throw new RuntimeException("Capacité insuffisante pour ces réservations !");
        }

        // Mise à jour de la capacité de l'événement
        event.setCapaciteMax(event.getCapaciteMax() - totalPlaces);

        // Associer les réservations à l'événement
        reservations.forEach(reservation -> reservation.setEvent(event));
        event.getReservations().addAll(reservations);

        return eventRepository.save(event);
    }
    @Override
    public Event affecterSponsorsToEvent(List<Long> idSponsors, Long idEvent) {
        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        List<Sponsor> sponsors = sponsorRepository.findAllById(idSponsors);

        if (sponsors.isEmpty()) {
            throw new RuntimeException("Aucun sponsor trouvé");
        }

        event.getSponsors().addAll(sponsors);
        return eventRepository.save(event);
    }

    @Override
    public Event affecterReviewsToEvent(List<Long> idReviews, Long idEvent) {
        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        List<Review> reviews = reviewRepository.findAllById(idReviews);

        if (reviews.isEmpty()) {
            throw new RuntimeException("Aucune review trouvée");
        }

        for (Review review : reviews) {
            review.setEvent(event);
            reviewRepository.save(review);  // Sauvegarder chaque review avec l'event mis à jour
        }

        return event;
    }
    @Override
    public Event addEvent(Event event) {
        // Sauvegarder d'abord l'événement sans sponsors pour obtenir un ID
        Event savedEvent = eventRepository.save(event);

        // Si des sponsors sont associés, les sauvegarder
        if (event.getSponsors() != null && !event.getSponsors().isEmpty()) {
            Set<Sponsor> managedSponsors = new HashSet<>();
            for (Sponsor sponsor : event.getSponsors()) {
                // S'assurer que le sponsor existe en base
                Sponsor managedSponsor = sponsorRepository.findById(sponsor.getIdSponsor())
                        .orElseThrow(() -> new RuntimeException("Sponsor not found with id: " + sponsor.getIdSponsor()));
                managedSponsors.add(managedSponsor);
            }
            savedEvent.setSponsors(managedSponsors);
            return eventRepository.save(savedEvent);
        }
        return savedEvent;
    }
    @Override
    public Event updateEventWithSponsors(Long idEvent, Event eventDetails, List<Long> sponsorIds) {
        Event event = eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Mettre à jour les champs de base
        event.setNomEvent(eventDetails.getNomEvent());
        event.setDescriptionEvent(eventDetails.getDescriptionEvent());
        event.setDateDebut(eventDetails.getDateDebut());
        event.setDateFin(eventDetails.getDateFin());
        event.setLieu(eventDetails.getLieu());
        event.setCapaciteMax(eventDetails.getCapaciteMax());
        event.setPrix(eventDetails.getPrix());
        event.setStatut(eventDetails.getStatut());
        event.setTypeEvenement(eventDetails.getTypeEvenement());

        // Mettre à jour l'image si fournie
        if (eventDetails.getImageEvent() != null) {
            event.setImageEvent(eventDetails.getImageEvent());
        }

        // Mettre à jour les sponsors si des IDs sont fournis
        if (sponsorIds != null && !sponsorIds.isEmpty()) {
            Set<Sponsor> sponsors = new HashSet<>(sponsorRepository.findAllById(sponsorIds));
            event.setSponsors(sponsors);
        }

        return eventRepository.save(event);
    }
    @Override
    public Event getEventWithReviews(Long idEvent) {
        return eventRepository.findByIdWithReviews(idEvent)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }





}