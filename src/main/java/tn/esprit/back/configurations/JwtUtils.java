package tn.esprit.back.configurations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private int expirationTime;

    // Générer le token en incluant un seul rôle
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Ajout du rôle unique dans les claims

        // Créer le token avec les claims
        return createToken(claims, username);
    }

    // Créer le token avec les informations de l'utilisateur
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Ajouter les claims
                .setSubject(subject) // Ajouter le nom d'utilisateur
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission du token
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Date d'expiration du token
                .signWith(getSignKey(), SignatureAlgorithm.HS512) // Signature du token
                .compact(); // Retourner le token
    }

    // Générer la clé secrète utilisée pour signer le token
    private Key getSignKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512); // Clé secrète de 512 bits
    }

    // Valider le token avec les informations de l'utilisateur
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token); // Extraire le nom d'utilisateur du token
        String roleFromToken = extractRole(token); // Extraire le rôle du token

        // Vérifier si le nom d'utilisateur et le rôle sont valides
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
                && userDetails.getAuthorities().stream().anyMatch(authority -> roleFromToken.equals(authority.getAuthority())));
    }

    // Vérifier si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date()); // Comparer la date d'expiration avec la date actuelle
    }

    // Extraire la date d'expiration du token
    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration); // Extraire la date d'expiration
    }

    // Extraire le nom d'utilisateur du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Extraire le nom d'utilisateur du token
    }
    private String getJwtFromRequestahmed(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + bearerToken);  // Log to debug
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Extract token without "Bearer " prefix
        }
        return null;
    }

    // Extraire le rôle du token
    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role")); // Extraire le rôle en tant que String
    }

    // Extraire des informations spécifiques des claims du token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extraire tous les claims
        return claimsResolver.apply(claims); // Appliquer la fonction pour extraire la valeur désirée
    }
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // The secret key to decode the token
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Extract the subject (username) from the token
    }

    // Extraire tous les claims du token
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Analyser le token
                .setSigningKey(getSignKey()) // Définir la clé de signature
                .parseClaimsJws(token) // Analyser les claims du token
                .getBody(); // Retourner les claims
    }
}