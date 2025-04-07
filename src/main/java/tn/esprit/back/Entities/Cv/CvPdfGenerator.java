package tn.esprit.back.Entities.Cv;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import tn.esprit.back.Entities.Cv.Cv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CvPdfGenerator {

    public static String generateAndSavePdf(Cv cv) throws IOException, IOException {
        String fileName = "cv_" + cv.getId() + ".pdf";
        String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String filePath = uploadDir + fileName;
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("CV Details").setBold().setFontSize(18));
        document.add(new Paragraph("Name: " + cv.getName()));
        document.add(new Paragraph("Skills: " + cv.getSkills()));
        document.add(new Paragraph("Experience: " + cv.getExperience()));
        document.add(new Paragraph("Education: " + cv.getEducation()));
        document.add(new Paragraph("Contact Info: " + cv.getContactinfo()));

        document.close();

        return fileName;
    }
}
