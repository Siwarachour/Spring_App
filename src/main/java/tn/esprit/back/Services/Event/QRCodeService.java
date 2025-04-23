package tn.esprit.back.Services.Event;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Event.Reservation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class QRCodeService {

    public byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public String generateQRCodeContent(Reservation reservation) {
        String baseUrl = "http://localhost:4200/reservation/verify";

        String eventName = "Unknown event";
        String location = "Not specified";
        String imageUrl = "";
        String eventDate = "Not specified";

        if (reservation.getEvent() != null) {
            eventName = reservation.getEvent().getNomEvent() != null
                    ? reservation.getEvent().getNomEvent().trim()
                    : "Unnamed event (ID: " + reservation.getEvent().getIdEvent() + ")";

            location = reservation.getEvent().getLieu() != null
                    ? reservation.getEvent().getLieu().trim()
                    : "Not specified";

            imageUrl = reservation.getEvent().getImageEvent() != null
                    ? reservation.getEvent().getImageEvent().trim()
                    : "";

            eventDate = reservation.getEvent().getDateDebut() != null
                    ? reservation.getEvent().getDateDebut().toString()
                    : "Not specified";
        }

        return String.format(
                "%s?id=%d&event=%s&date=%s&seats=%s&status=%s&location=%s&image=%s",
                baseUrl,
                reservation.getIdReservation(),
                URLEncoder.encode(eventName, StandardCharsets.UTF_8),
                URLEncoder.encode(eventDate, StandardCharsets.UTF_8),
                URLEncoder.encode(reservation.getSeatNumbers().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")), StandardCharsets.UTF_8),
                URLEncoder.encode(reservation.getStatutPaiement(), StandardCharsets.UTF_8),
                URLEncoder.encode(location, StandardCharsets.UTF_8),
                URLEncoder.encode(imageUrl, StandardCharsets.UTF_8)
        );
    }

}
