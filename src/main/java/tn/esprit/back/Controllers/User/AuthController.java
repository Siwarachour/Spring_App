package tn.esprit.back.Controllers.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.User.UserProfile;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Requests.ForgotPasswordRequest;
import tn.esprit.back.Requests.LoginRequests;
import tn.esprit.back.Services.User.RoleService;
import tn.esprit.back.Services.User.UserService;
import tn.esprit.back.configurations.JwtUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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
        user.setApprouve(true); // utilisateur non approuvé à l’inscription


        userRepository.save(user);
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequests loginRequest) {

        User user = userRepository.findByusername(loginRequest.getUsername());
        System.out.println("uuser" + user);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String role = user.getRole().getName().toString();


                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                System.out.println("Authenticated User: " + username);

                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getId(), user.getUsername(), role, user.getImageUrl()));
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

    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable int id) {
        User user = userRepository.findById(id).orElseThrow();
        String imageUrl = "http://localhost:8089/Projetback/uploads/" + user.getImageUrl(); // ou user.getImageUrl()

        UserProfile profile = new UserProfile(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                imageUrl,
                user.getProjetsCrees());

        return ResponseEntity.ok(profile);
    }


    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByusername(username);
        String imageUrl = "http://localhost:8089/Projetback/uploads/" + user.getImageUrl(); // ou user.getImageUrl()

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfile profile = new UserProfile(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                imageUrl,
                user.getAddress(),
                user.getPhone(),
                user.getProjetsCrees()

        );

        return ResponseEntity.ok(profile);
    }


    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/user/upload-image")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Vérifie si un fichier est envoyé
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier sélectionné.");
        }

        // Vérifie que le fichier est bien une image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Seuls les fichiers image sont autorisés.");
        }

        // Créer le dossier s'il n'existe pas
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Nettoyer le nom de fichier original
        String originalFileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

        // Extraire l'extension (ex: ".jpg", ".png")
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i >= 0) {
            extension = originalFileName.substring(i);
        }

        // Générer un nom de fichier unique
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Construire le chemin complet du fichier
        Path filePath = Paths.get(uploadDir, uniqueFileName);

        // Copier le fichier (remplace s'il existe)
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByusername(username);

        if (user != null) {
            // Associer l'image à l'utilisateur
            user.setImageUrl(uniqueFileName);
            userRepository.save(user);

            return ResponseEntity.ok("Fichier téléchargé avec succès : " + uniqueFileName);
        } else {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }
    }


    @GetMapping("/uploads/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        // Charger l'image depuis le serveur
        Path imagePath = Paths.get("C:/Users/ala/Desktop/pi/Spring_App/src/main/resources/uploads").resolve(imageName);
        Resource resource = new FileSystemResource(imagePath);

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Ou MediaType.IMAGE_PNG en fonction du type
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        // Vérification que les mots de passe correspondent
        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Les nouveaux mots de passe ne correspondent pas.");
        }

        // Obtenez l'utilisateur actuel à partir de l'authentification
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Récupérer l'utilisateur à partir du nom d'utilisateur
        User user = userRepository.findByusername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        // Vérifier si l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("L'ancien mot de passe est incorrect.");
        }

        // Encoder le nouveau mot de passe et mettre à jour l'utilisateur
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe changé avec succès.");
    }


    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {

        return userService.updateUser(id, user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @PutMapping("/users/{id}/toggle-approval")
    public User toggleUserApproval(@PathVariable int id) {
        return userService.toggleApproval(id);
    }

    @PostMapping("/users/add")
    public ResponseEntity<Map<String, String>> addUser(@RequestBody User user, HttpServletRequest request) {
        String csrfToken = request.getHeader("X-CSRF-TOKEN");
        log.info("CSRF Token: {}", csrfToken);
        log.info("Attempting to add user: {}", user.getUsername());

        Map<String, String> response = new HashMap<>();

        // Check if the username already exists
        if (userRepository.findByusername(user.getUsername()) != null) {
            response.put("error", "Username is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the role exists
        Role existingRole = roleRepository.findById(user.getRole().getId()).orElse(null);
        if (existingRole == null) {
            response.put("error", "Role with id " + user.getRole().getId() + " not found");
            return ResponseEntity.badRequest().body(response);
        }

        // Set the role and password
        user.setRole(existingRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setApprouve(true); // Automatically approve the user on registration

        // Save the user to the database
        userRepository.save(user);

        response.put("message", "User added successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        long totalUsers = userRepository.count(); // Nombre total d'utilisateurs
        long approvedUsers = userRepository.countByApprouveTrue(); // Nombre d'utilisateurs approuvés
        long nonApprovedUsers = userRepository.countByApprouveFalse(); // Nombre d'utilisateurs non approuvés

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("approvedUsers", approvedUsers);
        statistics.put("nonApprovedUsers", nonApprovedUsers);

        return ResponseEntity.ok(statistics);
    }


}
