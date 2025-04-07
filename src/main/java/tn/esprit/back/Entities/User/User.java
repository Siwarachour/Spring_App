package tn.esprit.back.Entities.User;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Role.Role;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)


public class User {

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
    @Column(nullable = true)
    private String resetToken;

    //private String role;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToMany(mappedBy = "createur")
    private List<Projet> projetsCrees;

    @Column(nullable = true)
    private boolean approuve = true; // Par défaut non approuvé


    @ManyToMany
    private List<Projet> projetsParticipes;

    @Lob
    private byte[] image;

    public boolean isApprouve() {
        return approuve;
    }

    public void setApprouve(boolean approuve) {
        this.approuve = approuve;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    public List<Projet> getProjetsCrees() {
        return projetsCrees;
    }

    public void setProjetsCrees(List<Projet> projetsCrees) {
        this.projetsCrees = projetsCrees;
    }

    public List<Projet> getProjetsParticipes() {
        return projetsParticipes;
    }

    public void setProjetsParticipes(List<Projet> projetsParticipes) {
        this.projetsParticipes = projetsParticipes;
    }

    public String getResetToken() {
        return resetToken;
    }


    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastmodifiedDate;*/

}
