package tn.esprit.back.Services.User;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private  final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByusername(username);
        if(user==null){
            throw new UsernameNotFoundException("user not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString())));
    }
    public User getConnectedUser() {
        // Retrieve the authentication object from the SecurityContextHolder
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            // If the principal is of type User, return the authenticated user
            return (User) principal;
        } else {
            // In case the principal is not of type User, handle accordingly
            throw new RuntimeException("User is not authenticated");
        }
    }

}