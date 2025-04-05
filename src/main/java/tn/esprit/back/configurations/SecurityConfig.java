package tn.esprit.back.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.esprit.back.Services.User.CustomUserDetailsService;
import tn.esprit.back.filter.JwtFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    // Bean pour l'encodeur de mot de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean pour l'AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // Bean pour configurer la sécurité des requêtes HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Remplace "cors()" avec la nouvelle méthode
                .csrf(csrf -> csrf.disable())  // Remplace "csrf()" avec la nouvelle méthode
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/**", "/oauth2/**").permitAll() // Permet l'accès aux routes d'authentification sans authentification
                        .requestMatchers("/api/auth/users/{id}/roles").hasRole("ADMIN")
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        // .requestMatchers("/api/auth/user").authenticated() // Assurez-vous que la route est accessible


                        .anyRequest().authenticated()  // Authentifie toutes les autres requêtes

                )
                .addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class); // Ajout du filtre JWT

        return httpSecurity.build();
    }
    // Bean pour configurer CORS (Cross-Origin Resource Sharing)
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Origine autorisée (CORS)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Méthodes autorisées
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // En-têtes autorisés
        configuration.setAllowCredentials(true); // Permet d'envoyer des informations d'identification

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique cette configuration CORS à toutes les URL
        return source;
    }
}