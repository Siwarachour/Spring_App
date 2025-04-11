package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CvService {
    private final CvRepo cvRepo;
    private final UserRepository userRepository;


    public Object addCv(Cv cv) {
        // Temporarily hardcoded username; you can later replace with dynamic user extraction
        User user = userRepository.findByUsername("ahmed2");

        if (user != null) {
            cv.setStudent(user);

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
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Cv getCvById(int id) {
        return cvRepo.findById(id).orElse(null);
    }
    public Cv findCvByUsername(String username) {
        // Find the user by username
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Return the CV associated with the user
            return cvRepo.findByStudent(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}