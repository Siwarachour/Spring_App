package tn.esprit.back.Services.Event;

import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Event.Reservation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class PdfGenerationService {

    @Autowired
    private QRCodeService qrCodeService;

    public byte[] generateReservationPdf(Reservation reservation) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfDocument pdf = new PdfDocument(new PdfWriter(baos))) {

            Document document = new Document(pdf);
            document.setMargins(40, 40, 40, 40);

            // Load fonts
            PdfFont fontBold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont fontRegular = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
            PdfFont fontTitle = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);

            // Header section with logo and title
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(30);

            // Company logo (handles missing logo gracefully)
            try {
                InputStream logoStream = getClass().getResourceAsStream("/static/OIP (1).jfif");                if (logoStream != null) {
                    ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());
                    Image logo = new Image(logoData)
                            .setWidth(100)
                            .setAutoScale(true)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT);
                    headerTable.addCell(new Cell().add(logo).setBorder(null).setPadding(5));
                } else {
                    headerTable.addCell(new Cell().add(new Paragraph("")).setBorder(null));
                }
            } catch (Exception e) {
                headerTable.addCell(new Cell().add(new Paragraph("")).setBorder(null));
            }

            // Header text
            Div headerText = new Div()
                    .add(new Paragraph("RESERVATION CONFIRMATION")
                            .setFont(fontTitle)
                            .setFontSize(20)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setMarginBottom(5))
                    .add(new Paragraph("Confirmation #: " + reservation.getIdReservation())
                            .setFont(fontRegular)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.GRAY));
            headerTable.addCell(new Cell().add(headerText)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null)
                    .setPadding(5));

            document.add(headerTable);

            // Decorative separator


            // QR Code generation
            String qrData = qrCodeService.generateQRCodeContent(reservation);
            if (qrData == null || qrData.isEmpty()) {
                throw new IllegalArgumentException("QR code data cannot be null or empty");
            }

            byte[] qrCodeImage;
            try {
                qrCodeImage = qrCodeService.generateQRCodeImage(qrData, 200, 200);
            } catch (WriterException e) {
                throw new IOException("Failed to generate QR code image", e);
            }

            if (qrCodeImage == null || qrCodeImage.length == 0) {
                throw new IOException("Generated QR code image is invalid");
            }

            // Main content table (2 columns)
            Table contentTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(40);

            // Left column - Reservation details
            Cell detailsCell = new Cell()
                    .add(createDetailsSection(reservation, fontBold, fontRegular))
                    .setBorder(null)
                    .setPadding(10);

            // Right column - QR Code
            ImageData qrImageData = ImageDataFactory.create(qrCodeImage);
            Image qrImage = new Image(qrImageData)
                    .setWidth(180)
                    .setHeight(180)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            Cell qrCell = new Cell()
                    .add(new Paragraph("Event Ticket")
                            .setFont(fontBold)
                            .setFontSize(14)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(10))
                    .add(qrImage)
                    .add(new Paragraph("Scan this code at entrance")
                            .setFont(fontRegular)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(10))
                    .setBorder(null)
                    .setPadding(15)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.1f);

            contentTable.addCell(detailsCell);
            contentTable.addCell(qrCell);
            document.add(contentTable);

            // Footer section
            Div footer = new Div()

                    .add(new Paragraph("Thank you for your reservation!")
                            .setFont(fontRegular)
                            .setFontSize(12)
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("For any questions, please contact WorkMate.com")
                            .setFont(fontRegular)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.GRAY)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setMarginTop(20);

            document.add(footer);

            document.close();
            return baos.toByteArray();
        }
    }

    private Div createDetailsSection(Reservation reservation, PdfFont fontBold, PdfFont fontRegular) {
        Div detailsDiv = new Div();

        // Event Information Section
        detailsDiv.add(createSectionHeader("EVENT INFORMATION", fontBold));

        detailsDiv.add(createDetailRow("Event:", reservation.getEvent().getNomEvent(), fontBold, fontRegular));

        detailsDiv.add(createDetailRow("Location:", reservation.getEvent().getLieu(), fontBold, fontRegular));

        // Reservation Details Section
        detailsDiv.add(createSectionHeader("RESERVATION DETAILS", fontBold));

        String seats = reservation.getSeatNumbers().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        detailsDiv.add(createDetailRow("Seats:", seats, fontBold, fontRegular));
        detailsDiv.add(createDetailRow("Status:", reservation.getStatutPaiement(), fontBold, fontRegular));
        detailsDiv.add(createDetailRow("Total Amount:", String.format("%.2f TND", reservation.getMontantTotal()),
                fontBold, fontRegular));

        // Customer Information Section (if available)
        if (reservation.getClientFirstName() != null || reservation.getClientLastName() != null) {
            detailsDiv.add(createSectionHeader("CUSTOMER INFORMATION", fontBold));

            String clientName = reservation.getClientFirstName() + " " + reservation.getClientLastName();
            detailsDiv.add(createDetailRow("Name:", clientName, fontBold, fontRegular));

            if (reservation.getClientEmail() != null) {
                detailsDiv.add(createDetailRow("Email:", reservation.getClientEmail(), fontBold, fontRegular));
            }

            if (reservation.getClientPhone() != null) {
                detailsDiv.add(createDetailRow("Phone:", reservation.getClientPhone(), fontBold, fontRegular));
            }
        }

        return detailsDiv;
    }

    private Paragraph createSectionHeader(String title, PdfFont fontBold) {
        return new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(ColorConstants.BLUE)
                .setMarginTop(15)
                .setMarginBottom(8);
    }

    private Paragraph createDetailRow(String label, String value, PdfFont fontBold, PdfFont fontRegular) {
        return new Paragraph()
                .add(new Text(label).setFont(fontBold).setFontSize(12))
                .add(new Text(value).setFont(fontRegular).setFontSize(12))
                .setMarginBottom(6);
    }
}