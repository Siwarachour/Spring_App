package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class CvService {
    private final CvRepo cvRepo;
    private final UserRepository userRepository;

    // Existing addCv method for saving and generating a CV PDF
    public Object addCv(Cv cv) {
        // Temporarily hardcoded username; you can later replace with dynamic user extraction
        // 1. Save CV first to get its ID
        Cv savedCv = cvRepo.save(cv);

        try {
            // 2. Generate PDF and get filename
            String fileName = CvPdfGenerator.generateAndSavePdf(savedCv);

            // 3. Build the download link
            String downloadLink = "http://localhost:8089/cv/download/" + savedCv.getId();
            savedCv.setPdfDownloadLink(downloadLink);

            // 4. Save again to persist the link
            cvRepo.save(savedCv);

            // 5. Return the whole object or just the link
            return savedCv.getId(); // or return downloadLink;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate CV PDF");
        }
    }

    // Method to retrieve CV by ID
    public Cv getCvById(int id) {
        return cvRepo.findById(id).orElse(null);
    }

    // Method to retrieve CV by username
    public Cv findCvByUsername(String username) {
        // Find the user by username
        User user = userRepository.findByusername(username);

        if (user != null) {
            // Return the CV associated with the user
            return cvRepo.findByStudent(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // New method to retrieve the CV as a PDF for viewing (not downloading)
    public ResponseEntity<InputStreamResource> getCvPdfForViewing(int cvId) throws Exception {
        Cv cv = cvRepo.findById(cvId).orElse(null);
        if (cv == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = "cv_" + cv.getId() + ".pdf";
        String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/"; // Ensure this path is correct
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
