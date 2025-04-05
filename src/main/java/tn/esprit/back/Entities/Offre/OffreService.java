package tn.esprit.back.Entities.Offre;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

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
        if (authentication != null && authentication.isAuthenticated()) {
            // Ensure we are dealing with the correct principal type
            Object principalL = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

                // Retrieve the user from the UserRepository based on the username
                User user = userRepository.findByUsername(username);

                if (user != null) {
                    // Set the connected user as 'rh' (responsible person) for the offer
                    offre.setRh(user);

                    // Save the offer and return its ID
                    return (long) offreRepo.save(offre).getId();
                } else {
                    // Handle the case where the user is not found
                    throw new RuntimeException("User not found in the database");
                }
            } else {
                // Handle the case where the principal is not the expected User type
                throw new RuntimeException("Authenticated principal is not of the expected type");
            }
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User is not authenticated");
        }
    }
}
