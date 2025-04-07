package tn.esprit.back.Controllers.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.User.UserProfile;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Requests.ForgotPasswordRequest;
import tn.esprit.back.Requests.JwtResponce;
import tn.esprit.back.Requests.LoginRequests;
import tn.esprit.back.Services.User.RoleService;
import tn.esprit.back.configurations.JwtUtils;

import java.io.IOException;
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

        User user = userRepository.findByusername(loginRequest.getUsername());
        System.out.println("uuser"+user);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String role = user.getRole().getName().toString();


                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                System.out.println("Authenticated User: " + username);

                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getUsername(),role));
                Authentication authenticationy = SecurityContextHolder.getContext().getAuthentication();


                System.out.println(authenticationy + " haaaaaa");
                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
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


        Role role = user.getRole();

        if (role == null) {
            return ResponseEntity.noContent().build();
        }

        String roleName = role.getName().toString();

        return ResponseEntity.ok(roleName);
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
        String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;
        sendResetPasswordEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok(Collections.singletonMap("message", "Reset email sent successfully"));
    }

    public void sendResetPasswordEmail(String to, String resetLink) {
        try {
            // Créer le message de réinitialisation du mot de passe
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Réinitialisation du mot de passe");
            helper.setText("Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink);

            // Envoi de l'email
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            // Log détaillé de l'erreur d'authentification
            e.printStackTrace();
            System.out.println("Erreur d'authentification : " + e.getMessage());
        } catch (MessagingException e) {
            // Log spécifique pour les erreurs de messagerie
            e.printStackTrace();
            System.out.println("Erreur de messagerie : " + e.getMessage());
        } catch (Exception e) {
            // Log générique pour toute autre erreur
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String resetToken, @RequestParam String newPassword) {
        // Recherche de l'utilisateur par son resetToken
        Optional<User> userOptional = userRepository.findByResetToken(resetToken);

        // Vérifier si l'utilisateur existe
        if (userOptional.isPresent()) {
            User user = userOptional.get();  // Récupère l'objet User de l'Optional

            // Encoder le nouveau mot de passe
            String encodedPassword = passwordEncoder.encode(newPassword);
            System.out.println("Mot de passe encodé: " + encodedPassword);  // Vérification dans les logs

            // Mettre à jour le mot de passe
            user.setPassword(encodedPassword);  // Enregistrer le mot de passe encodé

            // Sauvegarder l'utilisateur avec le nouveau mot de passe
            userRepository.save(user);

            // Retourner une réponse
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
        } else {
            // Si aucun utilisateur n'a ce token, retourner une erreur
            return ResponseEntity.status(404).body("Token de réinitialisation invalide ou expiré");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByusername(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfile profile = new UserProfile(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier si l'utilisateur est authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(403).body("Utilisateur non authentifié");
            }

            String username = authentication.getName();  // Le nom d'utilisateur est récupéré via getName()

            // Charger l'utilisateur depuis la base de données en utilisant le nom d'utilisateur
            User user = userRepository.findByusername(username);  // Assurez-vous que cette méthode existe dans votre UserRepository
            if (user == null) {
                return ResponseEntity.status(404).body("Utilisateur non trouvé");
            }

            // Vérifier si un fichier a été téléchargé
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body("Le fichier est vide");
            }

            // Enregistrer l'image (en la stockant en tant que tableau de bytes)
            user.setImage(file.getBytes());
            userRepository.save(user);  // Sauvegarder l'utilisateur avec l'image dans la base de données

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Échec du téléchargement de l'image");
        }
    }


    @PutMapping(value="/users/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image) {

        try {
            // Récupérer le nom d'utilisateur à partir du token JWT
            String username = authentication.getName();

            User user = userRepository.findByusername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
            }

            if (image.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le fichier est vide");
            }

            // Enregistrer l'image
            user.setImage(image.getBytes());
            userRepository.save(user);

            return ResponseEntity.ok("Image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec du téléchargement de l'image.");
        }
    }



}
