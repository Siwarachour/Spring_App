package tn.esprit.back.configurations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtils {

    private final UserRepository userRepository;
    @Value("${app.secret-key}")
    private String secretKey;



    @Value("${app.expiration-time}")
    private int expirationTime;

    public JwtUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String generateToken(int id, String username, String role,String imageUrl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        claims.put("role", role);

        claims.put("imageUrl",  imageUrl);
        return createToken(claims);
    }


    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    // Générer la clé secrète utilisée pour signer le token
    private Key getSignKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
    }

    public boolean validateTokenn(String token) {
        try {
            String username = extractUsername(token);  // Extract username from token
            return !isTokenExpired(token); // Check expiration
        } catch (Exception e) {
            return false;  // Return false if there's any issue with the token (e.g., invalid signature or expired)
        }
    }



    // Valider le token avec les informations de l'utilisateur
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token); // Extraire le nom d'utilisateur du token
        String roleFromToken = extractRole(token); // Extraire le rôle du token

        // Vérifier si le nom d'utilisateur et le rôle sont valides
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
                && userDetails.getAuthorities().stream().anyMatch(authority -> roleFromToken.equals(authority.getAuthority())));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> ((Number) claims.get("id")).longValue());
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> (String) claims.get("username"));
    }


    // Vérifier si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date()); // Comparer la date d'expiration avec la date actuelle
    }

    // Extraire la date d'expiration du token
    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration); // Extraire la date d'expiration
    }



    // Extraire le rôle du token
    public String extractRole(String token) {
        String role = extractClaim(token, claims -> (String) claims.get("role"));
        System.out.println("🔍 Rôle extrait du token : " + role); // LOG POUR DEBUG
        return role;
    }


    // Extraire des informations spécifiques des claims du token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extraire tous les claims
        return claimsResolver.apply(claims); // Appliquer la fonction pour extraire la valeur désirée
    }

    // Extraire tous les claims du token
   private Claims extractAllClaims(String token) {
        return Jwts.parser() // Analyser le token
                .setSigningKey(getSignKey()) // Définir la clé de signature
                .parseClaimsJws(token) // Analyser les claims du token
                .getBody(); // Retourner les claims
    }




}
