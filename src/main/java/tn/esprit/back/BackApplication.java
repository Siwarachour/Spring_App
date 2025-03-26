package tn.esprit.back;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import tn.esprit.back.configurations.CustomAuditorAware;

@SpringBootApplication
public class BackApplication {

    @Autowired
    private CustomAuditorAware customAuditorAware;

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    // Test the CustomAuditorAware functionality by simulating a logged-in user
    @PostConstruct
    public void testAuditorAware() {
        // Simulate a logged-in user
        String username = "testUser";
        UserDetails userDetails = User.withUsername(username)
                .password("password")
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Test the CustomAuditorAware to see if it picks the authenticated user
        System.out.println("Current auditor: " + customAuditorAware.getCurrentAuditor().orElse("No auditor found"));
    }
}
