package tn.esprit.back.Controllers.Event;


import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Event.Reservation;
import tn.esprit.back.Services.Event.EventEmailService;
import tn.esprit.back.Services.Event.IReservationService;
import tn.esprit.back.Services.Event.PdfGenerationService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/Reservation")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {
    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private IReservationService reservationService;
    @Autowired
    private EventEmailService eventEmailService;

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestParam Long eventId,
            @RequestBody List<Integer> seatNumbers) {
        try {
            Reservation reservation = reservationService.createReservation(eventId, seatNumbers);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   /* @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Reservation>> getReservationsByEvent(@PathVariable Long eventId) {
        List<Reservation> reservations = reservationService.findByEventId(eventId);
        System.out.println("Found " + reservations.size() + " reservations for event " + eventId);
        return ResponseEntity.ok(reservations);
    }*/

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

    @PostMapping("/with-payment")
    public ResponseEntity<?> createReservationWithPayment(@RequestBody Reservation reservation) {
        try {
            // Validate required fields
            if (reservation.getEvent() == null || reservation.getEvent().getIdEvent() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Validation Error",
                        "message", "Event ID is required"
                ));
            }

            if (reservation.getPaymentMethod() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Validation Error",
                        "message", "Payment method is required"
                ));
            }

            if ("card".equals(reservation.getPaymentMethod())) {
                if (reservation.getPaymentDetails() == null) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Validation Error",
                            "message", "Payment details are required for card payments"
                    ));
                }

                // Validate card details
                if (StringUtils.isEmpty(reservation.getPaymentDetails().getCardNumber())) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Validation Error",
                            "message", "Card number is required"
                    ));
                }
                if (StringUtils.isEmpty(reservation.getPaymentDetails().getExpiryDate())) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Validation Error",
                            "message", "Expiry date is required"
                    ));
                }
                if (StringUtils.isEmpty(reservation.getPaymentDetails().getCvv())) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Validation Error",
                            "message", "CVV is required"
                    ));
                }
            }

            Reservation createdReservation = reservationService.createReservationWithPayment(reservation);

            // Send appropriate email based on payment method
            try {
                if ("card".equals(createdReservation.getPaymentMethod())) {
                    eventEmailService.sendReservationConfirmation(createdReservation);
                } else {
                    eventEmailService.sendReservationConfirmation(createdReservation);
                }
            } catch (Exception e) {
                //log.error("Failed to send confirmation email for reservation {}", createdReservation.getIdReservation(), e);
                // Continue even if email fails - don't fail the whole operation
            }

            return ResponseEntity.ok(createdReservation);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Processing Error",
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getReservationPdf(@PathVariable Long id) throws IOException {
        Reservation reservation = reservationService.getReservationbyId(id).get();
        byte[] pdfBytes = pdfGenerationService.generateReservationPdf(reservation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reservation_" + id + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getReservationsByEvent(@PathVariable Long eventId) {
        try {
            List<Reservation> reservations = reservationService.findByEventId(eventId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to fetch reservations",
                            "message", e.getMessage(),
                            "details", e.getCause() != null ? e.getCause().getMessage() : "No additional details"
                    ));
        }
    }
}
