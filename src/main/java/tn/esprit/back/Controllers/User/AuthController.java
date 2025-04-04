package tn.esprit.back.Controllers.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import tn.esprit.back.Requests.ForgotPasswordRequest;
import tn.esprit.back.Requests.JwtResponce;
import tn.esprit.back.Requests.LoginRequests;
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
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    private final JavaMailSender mailSender;

    private final UserRepository userRepository;


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

     // Vérification du rôle unique
     Role existingRole = roleRepository.findById(user.getRole().getId()).orElse(null);
     if (existingRole == null) {
         response.put("error", "Role with id " + user.getRole().getId() + " not found");
         return ResponseEntity.badRequest().body(response);
     }

     user.setRole(existingRole); // Associer le rôle unique à l'utilisateur
     user.setPassword(passwordEncoder.encode(user.getPassword()));

     userRepository.save(user);
     response.put("message", "User registered successfully!");
     return ResponseEntity.ok(response);
 }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequests loginRequest) {
        // Vérifier si l'utilisateur existe dans la base de données
        User user = userRepository.findByusername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Récupérer le rôle de l'utilisateur (un seul rôle)
        String role = user.getRole().getName().toString(); // Assurez-vous que 'getRole()' retourne un seul rôle

        // Générer un token JWT avec le rôle unique
        String token = jwtUtils.generateToken(user.getUsername(), role);

        // Retourner le token dans la réponse
        return ResponseEntity.ok(new JwtResponce(token));
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

        // Récupérer le rôle de l'utilisateur (un seul rôle)
        Role role = user.getRole(); // Ici, vous récupérez un seul rôle, pas une liste.

        if (role == null) {
            return ResponseEntity.noContent().build(); // Optionnel: Retourner 204 si aucun rôle trouvé
        }

        // Extraire le nom du rôle (en tant que chaîne de caractères)
        String roleName = role.getName().toString();  // Conversion de l'énumération en chaîne de caractères

        return ResponseEntity.ok(roleName); // Renvoie le rôle sous forme de String
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email not found"));
        }

        // Générer un token et l'associer à l'utilisateur
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // Envoi de l'email avec le lien de réinitialisation
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        sendResetPasswordEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok(Collections.singletonMap("message", "Reset email sent successfully"));
    }

    private void sendResetPasswordEmail(String recipientEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Reset Your Password");
            helper.setText("Click on the following link to reset your password: " + resetLink, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending reset password email", e);
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String resetToken, @RequestParam String newPassword) {
        // Recherche de l'utilisateur par son resetToken
        Optional<User> userOptional = userRepository.findByResetToken(resetToken);

        // Vérifier si l'utilisateur existe
        if (userOptional.isPresent()) {
            User user = userOptional.get();  // Récupère l'objet User de l'Optional

            // Mettre à jour le mot de passe
            user.setPassword(passwordEncoder.encode(newPassword));  // Encoder le nouveau mot de passe

            // Sauvegarder l'utilisateur avec le nouveau mot de passe
            userRepository.save(user);

            // Retourner une réponse
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
        } else {
            // Si aucun utilisateur n'a ce token, retourner une erreur
            return ResponseEntity.status(404).body("Token de réinitialisation invalide ou expiré");
        }
    }


}
