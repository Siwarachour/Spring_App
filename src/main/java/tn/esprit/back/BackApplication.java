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


}
