package tn.esprit.back.Controllers.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import tn.esprit.back.Services.User.RoleService;
import tn.esprit.back.configurations.JwtUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")


public class AuthController {

    @Autowired
    private final RoleService roleService;  // Service pour gérer les rôles


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final tn.esprit.back.Repository.User.roleRepository roleRepository;


    /*   @PostMapping("/register")
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
   */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user, HttpServletRequest request) {
        String csrfToken = request.getHeader("X-CSRF-TOKEN");
        log.info("CSRF Token: {}", csrfToken);
        log.info("Tentative d'inscription pour l'utilisateur : {}", user.getUsername());

        Map<String, String> response = new HashMap<>();

        if (userRepository.findByusername(user.getUsername()) != null) {
            response.put("error", "Username is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérification des rôles
        Set<Role> roles = new HashSet<>();
        for (Role role : user.getRoles()) {
            Role existingRole = roleRepository.findById(role.getId()).orElse(null);
            if (existingRole != null) {
                roles.add(existingRole);
            } else {
                log.error("Role avec ID {} introuvable", role.getId());
                response.put("error", "Role with id " + role.getId() + " not found");
                return ResponseEntity.badRequest().body(response);
            }
        }

        if (roles.isEmpty()) {
            response.put("error", "At least one valid role must be provided.");
            return ResponseEntity.badRequest().body(response);
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("Login request received for username: " + user.getUsername());

        try {
            // Authentification avec UsernamePasswordAuthenticationToken
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Vérification si l'authentification est réussie
            if (authentication.isAuthenticated()) {
                // Récupère les rôles de l'utilisateur
                List<String> roles = getUserRoles(user.getUsername()); // Remplace par ta logique réelle

                // Génère le token avec les rôles
                String token = jwtUtils.generateToken(user.getUsername(), roles);

                // Prépare la réponse avec le token et le type Bearer
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", token);
                authData.put("type", "Bearer");

                return ResponseEntity.ok(authData);
            } else {
                return ResponseEntity.badRequest().body("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    // Méthode fictive pour récupérer les rôles de l'utilisateur
    private List<String> getUserRoles(String username) {
        // Ici, tu devras récupérer les rôles depuis la base de données ou ton service d'authentification
        return List.of("ROLE_USER", "ROLE_ADMIN"); // A adapter selon ta logique
    }






    @GetMapping("/welcome")
    public String welcome(OAuth2AuthenticationToken authentication) {
        return "Bienvenue, " + authentication.getPrincipal().getAttribute("name") + "!";
    }

    @GetMapping("/api/auth/users/{id}/roles")
    public ResponseEntity<?> getRolesForUser(@PathVariable int id) {
        // Récupérer l'utilisateur par son ID
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        // Extraire les noms des rôles (en tant que chaînes de caractères)
        List<Role> roles = new ArrayList<>(user.getRoles());
        List<String> roleNames = roles.stream()
                .map(role -> role.getName().toString())  // Conversion de l'énumération en chaîne de caractères
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleNames); // Renvoie les rôles sous forme de List<String>
    }




}