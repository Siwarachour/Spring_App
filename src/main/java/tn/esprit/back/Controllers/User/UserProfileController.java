package tn.esprit.back.Controllers.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public UserProfileController(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;  
    }



}
