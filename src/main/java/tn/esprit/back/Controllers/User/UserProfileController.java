package tn.esprit.back.Controllers.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserProfileController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /*@GetMapping("/profile")
    public ResponseEntity<UserProfile> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // Vous pouvez récupérer les informations de l'utilisateur à partir de l'objet `userPrincipal`
        UserProfile userProfile = customUserDetailsService.getUserProfile(userPrincipal.getUsername());
        return ResponseEntity.ok(userProfile);
    }*/
}