package tn.esprit.back.Services.Event;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.esprit.back.Entities.Event.Event;
import tn.esprit.back.Entities.Event.Reservation;
import tn.esprit.back.Repository.Event.EventRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Autowired
    private final EventRepository eventRepository;

    public void sendReservationConfirmation(Reservation reservation) {
        try {
            // Chargez l'événement avec toutes les relations nécessaires
            Event event = eventRepository.findByIdWithDetails(reservation.getEvent().getIdEvent())
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

            Map<String, Object> model = new HashMap<>();
            model.put("eventName", event.getNomEvent());
            LocalDateTime localDateTime = event.getDateDebut().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            model.put("eventDate", formatFrenchDate(localDateTime));
            model.put("seats", formatSeatNumbers(reservation.getSeatNumbers()));
            //model.put("qrCodeUrl", generateQrCodeUrl(reservation.getIdReservation()));
            model.put("clientName", reservation.getClientFirstName() + " " + reservation.getClientLastName());

            Context context = new Context(Locale.FRENCH);
            context.setVariables(model);

            String htmlContent = templateEngine.process("event-reservation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reservation.getClientEmail());
            helper.setSubject("Confirmation de réservation - " + event.getNomEvent());
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Échec d'envoi d'email pour la réservation {}", reservation.getIdReservation(), e);
            throw new RuntimeException("Erreur d'envoi d'email", e);
        }
    }

    private String formatFrenchDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy 'à' HH'h'mm", Locale.FRENCH);
        return date.format(formatter);
    }

    private String formatSeatNumbers(List<Integer> seats) {
        return seats.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private String generateQrCodeUrl(Long reservationId) {
        return "http://votre-api.com/api/reservations/" + reservationId + "/qrcode";
    }
}