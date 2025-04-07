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
        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal);

        // Check if the authentication object is valid (i.e., the user is authenticated)
//        if (authentication != null && authentication.isAuthenticated()) {
        // Ensure we are dealing with the correct principal type
//            Object principalL = authentication.getPrincipal();
//            if (principal instanceof org.springframework.security.core.userdetails.User) {
//                String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
//
//                // Retrieve the user from the UserRepository based on the username
//                User user = userRepository.findByUsername(username);
        User user = userRepository.findByUsername("ahmed2");

//                if (user != null) {
        // Set the connected user as 'rh' (responsible person) for the offer
        offre.setRh(user);

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