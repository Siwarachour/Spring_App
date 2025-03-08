package tn.esprit.back.Controllers.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.configurations.JwtUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final tn.esprit.back.Repository.User.roleRepository roleRepository;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByusername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already in use");
        }

        // Vérifier que les rôles existent dans la base de données
        Set<Role> roles = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role existingRole = roleRepository.findById(role.getId()).orElse(null);
            if (existingRole != null) {
                roles.add(existingRole);
            } else {
                return ResponseEntity.badRequest().body("Role with id " + role.getId() + " not found");
            }
        }
        user.setRoles(roles);

        // Encoder le mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Sauvegarder l'utilisateur avec les rôles
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
                    if (authentication.isAuthenticated()) {
                        Map<String, Object> authData = new HashMap<>();
                        authData.put("token", jwtUtils.generateToken(user.getUsername()));
                        authData.put("tupe", "Bearer");
                        return ResponseEntity.ok(authData);
                    }return ResponseEntity.badRequest().body("Invalid username or password");
    }catch (AuthenticationException e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
}


        @GetMapping("/welcome")
        public String welcome(OAuth2AuthenticationToken authentication) {
            return "Bienvenue, " + authentication.getPrincipal().getAttribute("name") + "!";
        }

}
