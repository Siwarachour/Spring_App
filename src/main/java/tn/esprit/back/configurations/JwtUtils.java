package tn.esprit.back.configurations;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,username);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(),SignatureAlgorithm.HS512)
                .compact();

    }


    private Key getSignKey() {
        // Générer une clé secrète sécurisée de 512 bits pour HS512
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !tisTokenExpired(token));


    }

    private boolean tisTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token) ;
        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
