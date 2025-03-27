package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

@RequiredArgsConstructor
@Service
public class CvService {
    private final CvRepo cvRepo;
    private final UserRepository userRepository;


    public Object addCv(Cv cv, Authentication connecteduser) {
        // Retrieve the authentication details from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        System.out.println(username);
        // Retrieve the user from the UserRepository based on the username
        User user = userRepository.findByusername(username); // Assuming your UserRepository has a method to find by username

        if (user != null) {
            // Set the connected user to the cv
            cv.setStudent(user);
            // Save the CV and return the saved ID
            return cvRepo.save(cv).getId();
        } else {
            // Handle the case where user is not found, return some error or exception
            throw new RuntimeException("User not found");
        }
    }

}