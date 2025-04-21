package tn.esprit.back.configurations;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Get user from database
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Generate JWT token
            String role = user.getRole().getName().name();
            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), role, user.getImageUrl());

            // Determine redirect URL based on user role
            String redirectUrl;
            if (role.equals(RoleName.ROLE_ADMIN.name())) {
                redirectUrl = "http://localhost:4201/back/dashboard?token=" + token;
            } else {
                redirectUrl = "http://localhost:4201?token=" + token;
            }

            // Redirect to frontend with token
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("http://localhost:4201/login?error=usernotfound");
        }
    }
}