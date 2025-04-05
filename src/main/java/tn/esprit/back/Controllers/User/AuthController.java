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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;
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
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})


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

        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            response.put("error", "Username is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("error", "Email is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the role is provided (not null)
        if (user.getRole() == null) {
            response.put("error", "Role is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérification du rôle unique
        Role existingRole = roleRepository.findById(user.getRole().getId()).orElse(null);
        if (existingRole == null) {
            response.put("error", "Role with id " + user.getRole().getId() + " not found");
            return ResponseEntity.badRequest().body(response);
        }

        // Associer le rôle unique à l'utilisateur
        user.setRole(existingRole);
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        userRepository.save(user);

        // Respond with a success message
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        // Access the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username); // Fetch the user object using the username
            return ResponseEntity.ok(user);  // Return user data as needed
        }

        return ResponseEntity.status(403).body("User not authenticated");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequests loginRequest) {
        // Vérifier si l'utilisateur existe dans la base de données
        User user = userRepository.findByUsername(loginRequest.getUsername());
        System.out.println("uuser"+user);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                // Print only the username of the authenticated user
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String role = user.getRole().getName().toString(); // Assurez-vous que 'getRole()' retourne un seul rôle

                // Print the username of the authenticated user
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                System.out.println("Authenticated User: " + username);

                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getUsername(),role));
                Authentication authenticationy = SecurityContextHolder.getContext().getAuthentication();

                // Print the authentication details (for debugging)
                System.out.println(authenticationy + " haaaaaa");
                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }




    // Méthode fictive pour récupérer les rôles de l'utilisateur
    private List<String> getUserRoles(String username) {
        // Ici, tu devras récupérer les rôles depuis la base de données ou ton service d'authentification
        return List.of("ROLE_ADMIN"); // A adapter selon ta logique
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





}