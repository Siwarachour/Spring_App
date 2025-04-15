package tn.esprit.back.Entities.Cv;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CvPdfGenerator {

    public static String generateAndSavePdf(Cv cv) throws IOException {
        String fileName = "cv_" + cv.getId() + ".pdf";
        String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) Files.createDirectories(path);

        String filePath = uploadDir + fileName;
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(0, 0, 0, 0);

        DeviceRgb primaryColor = new DeviceRgb(45, 55, 72);
        DeviceRgb lightBackground = new DeviceRgb(245, 245, 245);
        DeviceRgb highlight = new DeviceRgb(63, 81, 181);

        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth()
                .setHeight(PageSize.A4.getHeight());

        // LEFT COLUMN — Personal Info
        Cell leftCell = new Cell().setBackgroundColor(lightBackground).setPadding(20).setBorder(null);

        if (cv.getPhotoUrl() != null && !cv.getPhotoUrl().isEmpty()) {
            try {
                Image img = new Image(ImageDataFactory.create(cv.getPhotoUrl()));
                img.setWidth(100).setHeight(100).setMarginBottom(15);
                leftCell.add(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        leftCell.add(new Paragraph(cv.getName())
                .setBold().setFontSize(20)
                .setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10));

        if (cv.getEmail() != null) leftCell.add(infoParagraph("Email: " + cv.getEmail()));
        if (cv.getPhoneNumber() != null) leftCell.add(infoParagraph("Phone: " + cv.getPhoneNumber()));
        if (cv.getAddress() != null) leftCell.add(infoParagraph("Address: " + cv.getAddress()));
        if (cv.getLinkedinProfile() != null) leftCell.add(infoParagraph("LinkedIn: " + cv.getLinkedinProfile()));

        // RIGHT COLUMN — Professional Info
        Cell rightCell = new Cell().setBorder(null).setPadding(30);

        // Skills (Updated: Skills are displayed in a single line now)
        if (cv.getSkills() != null && !cv.getSkills().isEmpty()) {
            rightCell.add(sectionTitle("Skills", highlight));
            rightCell.add(new Paragraph(cv.getSkills()).setFontSize(12).setTextAlignment(TextAlignment.LEFT).setMarginBottom(5));
        }

        // Experiences (Updated: Each experience with a label)
        if (cv.getExperiences() != null && !cv.getExperiences().isEmpty()) {
            rightCell.add(sectionTitle("Experience", highlight));
            int experienceCount = 1; // To label each experience
            for (String exp : cv.getExperiences()) {
                // Add label and experience
                rightCell.add(new Paragraph("Experience " + experienceCount++)
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(highlight)
                        .setMarginTop(10));
                rightCell.add(new Paragraph("• " + exp)
                        .setFontSize(12)
                        .setMarginLeft(10)
                        .setMarginBottom(10)); // Adjusted margin for better separation
            }
        }

        // Education
        if (cv.getEducations() != null && !cv.getEducations().isEmpty()) {
            rightCell.add(sectionTitle("Education", highlight));
            for (String edu : cv.getEducations()) {
                rightCell.add(new Paragraph("• " + edu).setFontSize(12).setMarginLeft(10).setMarginBottom(5));
            }
        }

        // Projects
        if (cv.getProjects() != null && !cv.getProjects().isEmpty()) {
            rightCell.add(sectionTitle("Projects", highlight));
            for (String project : cv.getProjects()) {
                rightCell.add(new Paragraph("• " + project).setFontSize(12).setMarginLeft(10).setMarginBottom(5));
            }
        }

        // Languages
        if (cv.getLanguages() != null && !cv.getLanguages().isEmpty()) {
            rightCell.add(sectionTitle("Languages", highlight));
            rightCell.add(new Paragraph(String.join(", ", cv.getLanguages()))
                    .setFontSize(12)
                    .setMarginLeft(10)
                    .setMarginBottom(5));
        }

        // Hobbies
        if (cv.getHobbies() != null && !cv.getHobbies().isEmpty()) {
            rightCell.add(sectionTitle("Hobbies", highlight));
            rightCell.add(new Paragraph(String.join(", ", cv.getHobbies()))
                    .setFontSize(12)
                    .setMarginLeft(10)
                    .setMarginBottom(5));
        }

        // Certificates
        if (cv.getCertificate() != null && !cv.getCertificate().isEmpty()) {
            rightCell.add(sectionTitle("Certificates", highlight));
            for (String cert : cv.getCertificate().split(",")) {
                rightCell.add(new Paragraph("• " + cert.trim())
                        .setFontSize(12)
                        .setMarginLeft(10)
                        .setMarginBottom(5));
            }
        }

        mainTable.addCell(leftCell);
        mainTable.addCell(rightCell);

        document.add(mainTable);
        document.close();

        return fileName;
    }

    private static Paragraph infoParagraph(String text) {
        return new Paragraph(text)
                .setFontSize(10)
                .setMarginBottom(8)
                .setFontColor(new DeviceRgb(70, 70, 70));
    }

    private static Paragraph sectionTitle(String title, DeviceRgb color) {
        return new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setFontColor(color)
                .setMarginTop(20)
                .setMarginBottom(10);
    }
}
