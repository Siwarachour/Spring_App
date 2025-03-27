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

    public Object addOffre(Offre offre, Authentication connecteduser) {
        // Use the passed Authentication object to get the username
        String username = ((org.springframework.security.core.userdetails.User) connecteduser.getPrincipal()).getUsername();

        // Retrieve the user from the UserRepository based on the username
        User user = userRepository.findByusername(username);

        if (user != null) {
            // Set the connected user as 'rh' (responsible person) for the offer
            offre.setRh(user);

            // Save the offer and return its ID
            return offreRepo.save(offre).getId();
        } else {
            // Handle the case where the user is not found (this should not happen if authentication is working)
            throw new RuntimeException("User not found");
        }
    }
}
