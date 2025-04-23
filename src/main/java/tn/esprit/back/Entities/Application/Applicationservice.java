package tn.esprit.back.Entities.Application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Feedback.Feedback;
import tn.esprit.back.Entities.Feedback.FeedbackRepository;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class Applicationservice {

    private final ApplicationRepo applicationRepo; // Repository for Application entity
    private final UserRepository userRepository;     // Repository for User entity
    private final FeedbackRepository feedbackRepository;     // Repository for User entity

    public Object addApplication(Application application, Authentication connectedUser) {

        // Retrieve the user based on the username
        User user = userRepository.findByusername("siwar");
        if (user != null) {
            // Set the student (user) and their CV to the application
            if (!user.getCvs().isEmpty()) {
                Cv latestCv = user.getCvs().get(user.getCvs().size() - 1); // last added CV
                application.setCv(latestCv);
            } else {
                throw new RuntimeException("User has no CVs.");
            }


            // Automatically assign Feedback with id = 2
            Feedback feedback = feedbackRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Feedback with ID 2 not found"));

            application.setFeedback(feedback);

            // Save the application and return its ID
            return applicationRepo.save(application).getId();

        } else {
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
