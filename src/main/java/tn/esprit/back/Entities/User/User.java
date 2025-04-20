package tn.esprit.back.Entities.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.back.Entities.Application.Application;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Offre.Offre;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.Role.Role;
import tn.esprit.back.Entities.coursCertificat.Cours;
import tn.esprit.back.Entities.library.Department;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.Review;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
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

    private boolean enabled = true;
    private boolean accountLocked = false;

    @Column(nullable = true)
    private String resetToken;

    private String description;
    private boolean approuve = true;

    @Column(nullable = true)
    private String imageUrl;

    // ===================== Role =====================
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // ===================== Offre & Application =====================
    @OneToMany
    private Set<Offre> offres;

    @OneToMany
    private Set<Application> applications;

    // ===================== CV =====================
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private Cv cv;

    // ===================== Projet Relations =====================
    @JsonIgnore
    @OneToMany(mappedBy = "createur")
    private List<Projet> projetsCrees;

    @JsonIgnore
    @ManyToMany
    private List<Projet> projetsParticipes;

    @JsonIgnore
    @ManyToMany(mappedBy = "membres")
    private List<Projet> projetsParticipe;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur")
    private List<Tache> tachesAssignees;

    // ===================== Cours =====================
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Cours> cours;

    // ===================== Library Module =====================
    @JsonIgnore
    @ManyToOne
    private Department department;

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviews = new ArrayList<>();

    // ===================== UserDetails =====================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // ===================== Principal =====================
    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
