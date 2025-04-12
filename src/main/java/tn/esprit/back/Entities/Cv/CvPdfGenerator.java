package tn.esprit.back.Entities.Cv;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CvPdfGenerator {

    public static String generateAndSavePdf(Cv cv) throws IOException {
        // Define the file name and upload directory
        String fileName = "cv_" + cv.getId() + ".pdf";
        String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";

        // Create the directory if it doesn't exist
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String filePath = uploadDir + fileName;
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add title section with simulated centering using spaces
        String titleText = "Curriculum Vitae";
        int padding = 20; // Adjust padding to "center" the text
        String centeredTitle = String.format("%" + padding + "s", titleText);
        Paragraph title = new Paragraph(centeredTitle)
                .setBold()
                .setFontSize(22)
                .setMarginTop(50)  // Adjust top margin for spacing
                .setMarginBottom(20);  // Adjust bottom margin for spacing
        document.add(title);

        // Add Name
        Paragraph nameSection = new Paragraph("Name: " + cv.getName())
                .setBold()
                .setFontSize(16);
        document.add(nameSection);

        // Add Skills Section
        Paragraph skillsTitle = new Paragraph("Skills:")
                .setBold()
                .setFontSize(16);
        document.add(skillsTitle);
        String[] skills = cv.getSkills().split(",");
        for (String skill : skills) {
            document.add(new Paragraph("- " + skill.trim()));
        }

        // Add Experience Section
        Paragraph experienceTitle = new Paragraph("Experience:")
                .setBold()
                .setFontSize(16);
        document.add(experienceTitle);
        document.add(new Paragraph(cv.getExperience()));

        // Add Education Section
        Paragraph educationTitle = new Paragraph("Education:")
                .setBold()
                .setFontSize(16);
        document.add(educationTitle);
        document.add(new Paragraph(cv.getEducation()));

        // Add Contact Information Section
        Paragraph contactTitle = new Paragraph("Contact Information:")
                .setBold()
                .setFontSize(16);
        document.add(contactTitle);
        document.add(new Paragraph(cv.getContactinfo()));

        // Close the document
        document.close();

        return fileName;
    }
}
