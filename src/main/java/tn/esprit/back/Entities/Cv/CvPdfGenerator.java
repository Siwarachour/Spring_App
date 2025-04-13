package tn.esprit.back.Entities.Cv;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

        // Extract all words from the CV content
        List<String> words = extractWords(cv);
        System.out.println("All words in the CV: " + words);

        // Store the extracted words in the Cv entity
        cv.setExtractedWords(words);  // Set the extracted words in the Cv entity

        // Save the CV entity to the database (assuming you have a repository or service to do this)
        // cvRepository.save(cv);  // Uncomment and use your repository to save the Cv

        // Close the document
        document.close();

        return fileName;
    }

    private static List<String> extractWords(Cv cv) {
        List<String> words = new ArrayList<>();
        // Extract words from all sections of the CV
        extractWordsFromText(cv.getName(), words);
        extractWordsFromText(cv.getSkills(), words);
        extractWordsFromText(cv.getExperience(), words);
        extractWordsFromText(cv.getEducation(), words);
        extractWordsFromText(cv.getContactinfo(), words);

        return words;
    }

    private static void extractWordsFromText(String text, List<String> words) {
        if (text != null && !text.trim().isEmpty()) {
            // Split the text into words and add them to the list
            String[] wordArray = text.toLowerCase().split("\\W+");
            for (String word : wordArray) {
                if (!word.isEmpty()) {
                    words.add(word);  // Add non-empty words to the list
                }
            }
        }
    }

}
