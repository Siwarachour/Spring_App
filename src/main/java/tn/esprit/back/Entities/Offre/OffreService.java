package tn.esprit.back.Entities.Offre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Services.User.CustomUserDetailsService;

@RequiredArgsConstructor
@Service
public class OffreService {
    private final OffreRepo offreRepo;
    private final UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService userService;


    public Object addOffre(Offre offre) {
        // Get the connected user using the UserService
        User connectedUser = userService.getConnectedUser();

        // Now you can access the username or any other details
        String username = connectedUser.getUsername();
        System.out.println("Connected user: " + username);

        // Proceed with your logic to add an offer
        // For example, set the connected user as the "responsible person" for the offer
        User user = userRepository.findByusername(username);
        if (user != null) {
            offre.setRh(user);
            return offreRepo.save(offre).getId();
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
