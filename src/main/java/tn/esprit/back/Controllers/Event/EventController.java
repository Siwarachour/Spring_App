package tn.esprit.back.Controllers.Event;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;


import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Event.Event;
import tn.esprit.back.Entities.Event.Reservation;
import tn.esprit.back.Entities.Event.Sponsor;
import tn.esprit.back.Repository.Event.EventRepository;
import tn.esprit.back.Repository.Event.SponsorRepository;
import tn.esprit.back.Services.Event.IEventService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/Event")
public class EventController {
    @Autowired
    IEventService EventService;
    @Autowired
    SponsorRepository sponsorRepository;
    @Autowired
    EventRepository eventRepository;



    @GetMapping("/retriveAllEvents")
    List<Event> retriveAllEvents() {
        return EventService.retriveAllEvents();
    }

    @GetMapping("/retriveEvent/{idEvent}")
    Event getEventById(@PathVariable long idEvent) {
        return EventService.getEventbyId(idEvent);
    }

    @DeleteMapping("/deleteEvent/{idEvent}")
    void deleteEvent(@PathVariable long idEvent) {
        EventService.deleteEvent(idEvent);
    }

    @PutMapping("/effectereservationToEvent/{idReservation}/{idEvent}")
    public Event effectereservationToEvent(@PathVariable List<Long> idReservation, @PathVariable Long idEvent) {
        return EventService.effectereservationToEvent(idReservation, idEvent);
    }
    @PutMapping("/affecterSponsorsToEvent/{idSponsors}/{idEvent}")
    public Event affecterSponsorsToEvent(
            @PathVariable List<Long> idSponsors,
            @PathVariable Long idEvent) {
        return EventService.affecterSponsorsToEvent(idSponsors, idEvent);
    }
    @PutMapping("/affecterReviewsToEvent/{idReviews}/{idEvent}")
    public Event affecterReviewsToEvent(
            @PathVariable List<Long> idReviews,
            @PathVariable Long idEvent) {
        return EventService.affecterReviewsToEvent(idReviews, idEvent);
    }


    private String storeImage(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(uploadDir + fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }
    @PutMapping(value = "/updateEventWithImage/{idEvent}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> updateEventWithImage(
            @PathVariable Long idEvent,
            @RequestPart("event") String eventStr,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Event eventDetails = mapper.readValue(eventStr, Event.class);

        Event existingEvent = EventService.getEventbyId(idEvent);

        // Mettre à jour les champs
        existingEvent.setNomEvent(eventDetails.getNomEvent());
        existingEvent.setDescriptionEvent(eventDetails.getDescriptionEvent());
        existingEvent.setDateDebut(eventDetails.getDateDebut());
        existingEvent.setDateFin(eventDetails.getDateFin());
        existingEvent.setLieu(eventDetails.getLieu());
        existingEvent.setCapaciteMax(eventDetails.getCapaciteMax());
        existingEvent.setPrix(eventDetails.getPrix());
        existingEvent.setStatut(eventDetails.getStatut());
        existingEvent.setTypeEvenement(eventDetails.getTypeEvenement());

        // Mettre à jour l'image seulement si un nouveau fichier est fourni
        if (file != null && !file.isEmpty()) {
            String fileName = storeImage(file);
            existingEvent.setImageEvent(fileName);
        }


        Event updatedEvent = EventService.updateEvent(idEvent, existingEvent);
        return ResponseEntity.ok(updatedEvent);
    }
    @PostMapping(value = "/addEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Event addEventWithImageAndSponsors(
            @RequestPart("event") String eventStr,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "sponsorIds", required = false) String sponsorIdsStr) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Event event = mapper.readValue(eventStr, Event.class);

        if (file != null && !file.isEmpty()) {
            String fileName = storeImage(file);
            event.setImageEvent(fileName);
        }

        // Gestion des sponsors
        if (sponsorIdsStr != null && !sponsorIdsStr.isEmpty()) {
            List<Long> sponsorIds = mapper.readValue(sponsorIdsStr, new TypeReference<List<Long>>() {});
            if (!sponsorIds.isEmpty()) {
                List<Sponsor> sponsors = sponsorRepository.findAllById(sponsorIds);
                event.setSponsors(new HashSet<>(sponsors));
            }
        }

        return EventService.addEvent(event);
    }
    /*@GetMapping("/retriveAllEventsWithSponsors")
    public ResponseEntity<List<Event>> getAllEventsWithSponsors() {
        List<Event> events = eventRepository.findAllWithSponsors();
        return ResponseEntity.ok(events);
    }*/
    @GetMapping("/retriveAllEventsWithSponsors")
    public ResponseEntity<?> getAllEventsWithSponsors() {
        try {
            List<Event> events = eventRepository.findAllWithSponsors(); // JOIN FETCH possible
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            e.printStackTrace(); // 🔍 À regarder dans la console !
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }

    @PutMapping(value = "/updateEventWithImageAndSponsors/{idEvent}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Event> updateEventWithImageAndSponsors(
            @PathVariable Long idEvent,
            @RequestPart("event") String eventStr,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "sponsorIds", required = false) String sponsorIdsStr) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Event eventDetails = mapper.readValue(eventStr, Event.class);

        List<Long> sponsorIds = null;
        if (sponsorIdsStr != null && !sponsorIdsStr.isEmpty()) {
            sponsorIds = mapper.readValue(sponsorIdsStr, new TypeReference<List<Long>>() {});
        }

        // Mettre à jour l'image si fournie
        if (file != null && !file.isEmpty()) {
            String fileName = storeImage(file);
            eventDetails.setImageEvent(fileName);
        }

        Event updatedEvent = EventService.updateEventWithSponsors(idEvent, eventDetails, sponsorIds);
        return ResponseEntity.ok(updatedEvent);
    }


    @GetMapping("/retriveEventWithReviews/{idEvent}")
    public ResponseEntity<Event> getEventWithReviews(@PathVariable Long idEvent) {
        Event event = EventService.getEventWithReviews(idEvent);

        System.out.println("Reviews count: " + event.getReviews().size());
        event.getReviews().forEach(r -> System.out.println(r.getIdReview() + ": " + r.getCommentaire()));

        return ResponseEntity.ok(event);
    }
    @GetMapping("/{eventId}/reservations")
    public ResponseEntity<List<Reservation>> getEventReservations(@PathVariable Long eventId) {
        List<Reservation> reservations = EventService.getReservationsByEventId(eventId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = EventService.getEventById(id);
        return ResponseEntity.ok(event);
    }



}
