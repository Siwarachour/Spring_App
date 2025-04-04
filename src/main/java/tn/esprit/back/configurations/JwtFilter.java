package tn.esprit.back.configurations;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    public JwtFilter(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println(bearerToken+"testf");  // Print the full Authorization header for debugging
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Extract the token without "Bearer " prefix
        }
        return null;  // Return null if no token is found
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtFilter is processing request");
        String token = getJwtFromRequest(request);
        System.out.println("Extracted Token: " + token);  // Debug log

        if (token != null) {
            try {
                String username = jwtUtils.extractUsername(token);
                System.out.println("Extracted Username: " + username);  // Debug log

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token, userDetails)) {
                    String role = jwtUtils.extractRole(token);
                    System.out.println("Extracted Role: " + role);  // Debug log

                    Collection<SimpleGrantedAuthority> roles = (role != null) ?
                            List.of(new SimpleGrantedAuthority(role)) : List.of();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, roles);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication Successful");  // Debug log
                }
            } catch (ExpiredJwtException e) {
                System.out.println("Token Expired");
                handleJwtException(response, "Token expired");
                return;
            } catch (MalformedJwtException e) {
                System.out.println("Malformed Token");
                handleJwtException(response, "Malformed token");
                return;
            } catch (Exception e) {
                System.out.println("Token Validation Failed: " + e.getMessage());
                handleJwtException(response, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }



    // Méthode pour gérer les erreurs liées au jeton
    private void handleJwtException(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}