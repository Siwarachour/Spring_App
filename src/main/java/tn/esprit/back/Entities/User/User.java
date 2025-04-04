package tn.esprit.back.Entities.User;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.back.Entities.Application.Application;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Offre.Offre;
import tn.esprit.back.Entities.Role.Role;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Data
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phone;
    private String address;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    private boolean enabled = true; // Default to true for new users
    private boolean accountLocked = false;


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @OneToMany
    private Set<Offre> offres;

    @OneToMany
    private Set<Application> applications;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private Cv cv;

    // Principal method
    @Override
    public String getName() {
        return username;
    }

    // UserDetails methods
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;  // You can add more logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // You can add more logic if needed
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
