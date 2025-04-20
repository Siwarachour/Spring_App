package tn.esprit.back.Entities.Offre;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OffreService {
    private final OffreRepo offreRepo;
    private final UserRepository userRepository;

    // Corrected image folder path to be outside of src/main/java
    private final String IMAGE_FOLDER = System.getProperty("user.dir") + "/uploads/offreimg/";

    @CrossOrigin(origins = "http://localhost:4201", allowedHeaders = "*", allowCredentials = "true")
    public Long addOffre(Offre offre, MultipartFile image) {
        // Authentication check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated access!");
        }

        String username = authentication.getName();
        System.out.println("Username from JWT: " + username);

        // Define the new image folder path
        String IMAGE_FOLDER = "C:\\Users\\21650\\Desktop\\Spring_App-merge2\\src\\main\\resources\\uploads\\offreimages\\";

        // Handle image saving
        if (image != null && !image.isEmpty()) {
            try {
                // Ensure the directory exists
                File directory = new File(IMAGE_FOLDER);
                if (!directory.exists()) {
                    directory.mkdirs();  // Create directories if they don't exist
                }

                // Generate a unique file name and save the image
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                File dest = new File(IMAGE_FOLDER + fileName);
                image.transferTo(dest);

                // Set the image path in the entity (relative path for frontend access)
                offre.setImageUrl("uploads/offreimages/" + fileName);  // relative path for frontend
            } catch (IOException e) {
                throw new RuntimeException("Error saving image: " + e.getMessage());
            }
        }

        // Save the offer and return the ID
        return (long) offreRepo.save(offre).getId();
    }

    // Get all offers
    public List<Offre> getAllOffres() {
        return offreRepo.findAll();
    }

    // Get offer by ID
    public Offre getOffreById(Long id) {
        Optional<Offre> offre = offreRepo.findById(Math.toIntExact(id));
        return offre.orElse(null);
    }

    // Edit existing offer
    public Offre editOffre(Long id, Offre updatedOffre) {
        Optional<Offre> existingOffre = offreRepo.findById(Math.toIntExact(id));
        if (existingOffre.isPresent()) {
            Offre offre = existingOffre.get();
            offre.setTitle(updatedOffre.getTitle());
            offre.setSkills(updatedOffre.getSkills());
            offre.setDescription(updatedOffre.getDescription());

            // Update only specific fields, not the 'createdAt' or 'updatedAt'
            offre.setCreatedAt(LocalDateTime.now());  // If you want to update the timestamp

            return offreRepo.save(offre);
        }
        return null;
    }


    // Delete offer
    public boolean deleteOffre(Long id) {
        if (offreRepo.existsById(Math.toIntExact(id))) {
            offreRepo.deleteById(Math.toIntExact(id));
            return true;
        }
        return false;
    }
}
