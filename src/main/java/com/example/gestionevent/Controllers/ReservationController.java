package com.example.gestionevent.Controllers;

import com.example.gestionevent.Services.IReservationService;
import com.example.gestionevent.Services.ReservationServiceImplement;
import com.example.gestionevent.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/Reservation")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestParam Long eventId,
            @RequestBody List<Integer> seatNumbers) {
        try {
            Reservation reservation = reservationService.createReservation(eventId, seatNumbers);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Reservation>> getReservationsByEvent(@PathVariable Long eventId) {
        List<Reservation> reservations = reservationService.findByEventId(eventId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationbyId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.retriveAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservation) {
        reservation.setIdReservation(id);
        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}