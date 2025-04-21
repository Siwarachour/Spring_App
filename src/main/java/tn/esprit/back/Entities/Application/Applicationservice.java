package tn.esprit.back.Entities.Application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class Applicationservice {

    private final ApplicationRepo applicationRepo; // Repository for Application entity
    private final UserRepository userRepository;     // Repository for User entity

    public Object addApplication(Application application, Authentication connectedUser) {
        // Retrieve the authentication details from the SecurityContext


        // Retrieve the user from the UserRepository based on the username
        User user = userRepository.findByusername("alaboss1"); // Assuming your UserRepository has a method to find by username
        if (user != null) {
            // Set the connected user to the application
            application.setStudent(user);  // Assuming Application has a reference to User (e.g. setUser)

            // Save the Application and return the saved ID or something else you want
            return applicationRepo.save(application).getId();
        } else {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found");
        }
    }

    public List<User> getUsersWithAcceptedApplications() {
        return applicationRepo.findUsersWithAcceptedApplications();
    }



    public List<Application> getApplications() {
        return applicationRepo.findAll();
    }
    // Add other methods as needed
    // For example:
    // public void updateApplication(Application application) { ... }
    // public List<Application> getAllApplications
    // You could add other methods like updateApplication, getApplications, etc. depending on your needs


    public Application updateInterviewStatus(Integer applicationId, InterviewStatus status) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setResult(status);
        return applicationRepo.save(app);
    }
}
