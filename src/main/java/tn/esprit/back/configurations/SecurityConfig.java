package tn.esprit.back.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.esprit.back.Services.User.CustomUserDetailsService;
import tn.esprit.back.filter.JwtFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()


                                        // Autoriser l'accès public aux items en GET
                                        .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()

                                        // Routes clients
                                        .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "ADMIN")

                                        // Routes admin
                                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                // Correction des patterns pour les items
                                .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/items/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/items").hasRole("CLIENT")
                                .requestMatchers(HttpMethod.PUT, "/api/items/{id}").hasRole("CLIENT")
                                .requestMatchers(HttpMethod.DELETE, "/api/items/{id}").hasRole("CLIENT")
                                .requestMatchers("/api/items/pending").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/items/{id}/approve").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/items/{id}/reject").hasRole("ADMIN")


                                        // User management
                        .requestMatchers("/api/auth/users/add").hasRole("ADMIN")
                        .requestMatchers("/api/auth/users/**").authenticated()
                        .requestMatchers("/api/auth/profile").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                       // .requestMatchers("/api/items/**").permitAll()
// Items endpoints - GET public, others require auth
//                                .requestMatchers(String.valueOf(HttpMethod.GET), "/api/items/**").permitAll()
//                                .requestMatchers(String.valueOf(HttpMethod.POST), "/api/items").permitAll()
//                                .requestMatchers(String.valueOf(HttpMethod.PUT), "/api/items/**").permitAll()
//                                .requestMatchers(String.valueOf(HttpMethod.DELETE), "/api/items/**").permitAll()

                                .requestMatchers("/api/auth/profile/image").authenticated()
                        .requestMatchers("/api/auth/users/upload-image").authenticated()
                        .requestMatchers("/api/auth/users/{username}/upload-image").permitAll()
                                .requestMatchers("/api/projets").permitAll()
                        .requestMatchers("/api/projets/**").authenticated()

                        .requestMatchers("/api/taches").authenticated()
                        .requestMatchers("/api/taches/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:4201"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "X-CSRF-TOKEN"
        ));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}