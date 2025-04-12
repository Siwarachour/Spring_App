package tn.esprit.back.Entities.Offre;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OffreService {
    private final OffreRepo offreRepo;
    private final UserRepository userRepository;

    public Long addOffre(Offre offre) {
        // Get the authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated access!");
        }
        System.out.println();
        // Get the username of the authenticated user
        String username = authentication.getName();
        System.out.println(username);// This will be the username from the JWT
        System.out.println("Username from JWT: " + username);
        // Fetch the full User entity from the database



        // Save the offer and return its ID
        return (long) offreRepo.save(offre).getId();
    }

    // Get all offres
    public List<Offre> getAllOffres() {
        return offreRepo.findAll();
    }

    // Get an offer by ID
    public Offre getOffreById(Long id) {
        Optional<Offre> offre = offreRepo.findById(Math.toIntExact(id));
        return offre.orElse(null);  // Return null if not found
    }

    // Edit an existing offer
    public Offre editOffre(Long id, Offre updatedOffre) {
        Optional<Offre> existingOffre = offreRepo.findById(Math.toIntExact(id));
        if (existingOffre.isPresent()) {
            Offre offre = existingOffre.get();
            // Update fields of the existing offer
            offre.setTitle(updatedOffre.getTitle());
            offre.setSkills(updatedOffre.getSkills());
            offre.setDescription(updatedOffre.getDescription());
            return offreRepo.save(offre);
        }
        return null;  // Return null if the offer is not found
    }

    // Delete an offer
    public boolean deleteOffre(Long id) {
        if (offreRepo.existsById(Math.toIntExact(id))) {
            offreRepo.deleteById(Math.toIntExact(id));
            return true;
        }
        return false;
    }
}