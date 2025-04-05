package tn.esprit.back.configurations;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tn.esprit.back.Entities.Role.Role;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private int expirationTime;

    // Generate token
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Add role to claims
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSignKey() {
        // Use the secretKey loaded from application.properties or application.yml
        return new javax.crypto.spec.SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        String roleFromToken = extractRole(token);
        System.out.println("Role from token: " + roleFromToken);
        System.out.println("userDetails.getAuthorities(): " + userDetails.getAuthorities());

        // Log the class of the authority object to check if it's correctly returning the Role object
        userDetails.getAuthorities().forEach(authority -> {
            System.out.println("Authority class type: " + authority.getClass());
            System.out.println("Authority value: " + authority.getAuthority());
        });

        // Convert authorities to a list of strings
        List<String> roleNames = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority()) // Extract the role name (a string)
                .collect(Collectors.toList());

        // Print the list of role names (as strings)
        System.out.println("User details roles: " + roleNames);
        System.out.println("roleuserconnecte: " + roleNames);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
                && roleNames.stream().anyMatch(roleFromToken::equals));  // Compare the role from token with the authorities
    }





    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpirationDate(token);
        System.out.println("Token expiration date: " + expirationDate);
        return expirationDate.before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
