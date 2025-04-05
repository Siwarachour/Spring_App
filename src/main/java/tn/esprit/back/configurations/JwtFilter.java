package tn.esprit.back.configurations;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    public JwtFilter(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Extract the token without "Bearer " prefix
        }
        return null;  // Return null if no token is found
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the token
        String token = getJwtFromRequest(request);

        if (token != null) {
            try {
                String username = jwtUtils.extractUsername(token);
                System.out.println("username from token"+username);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                System.out.println(userDetails+"eeee");
                if (jwtUtils.validateToken(token, userDetails)) {
                    String role = jwtUtils.extractRole(token);
                    System.out.println("role from token"+role);
                    Collection<SimpleGrantedAuthority> roles = (role != null) ?
                            List.of(new SimpleGrantedAuthority(role)) : List.of();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, roles);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                handleJwtException(response, "Token expired");
                return;
            } catch (MalformedJwtException e) {
                handleJwtException(response, "Malformed token");
                return;
            } catch (Exception e) {
                handleJwtException(response, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
