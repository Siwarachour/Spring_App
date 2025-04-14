package tn.esprit.back.Entities.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Transaction;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.Role.Role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    // Getters et Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String address;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    private boolean enabled = true; // Default to true for new users
    private boolean accountLocked = false;
    @Column(nullable = true)
    private String resetToken;
private String description;
    //private String role;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToMany(mappedBy = "createur")
    @JsonIgnore

    private List<Projet> projetsCrees;

    @Column(nullable = true)
    private boolean approuve = true; // Par défaut non approuvé


    @ManyToMany
    private List<Projet> projetsParticipes;

    @Column(nullable = true)
    private String imageUrl;  // Ajoute cet attribut


    @ManyToMany(mappedBy = "membres")
    private List<Projet> projetsParticipe;

    @OneToMany(mappedBy = "utilisateur")
    private List<Tache> tachesAssignees;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Projet> getProjetsParticipe() {
        return projetsParticipe;
    }

    public void setProjetsParticipe(List<Projet> projetsParticipe) {
        this.projetsParticipe = projetsParticipe;
    }

    public List<Tache> getTachesAssignees() {
        return tachesAssignees;
    }

    public void setTachesAssignees(List<Tache> tachesAssignees) {
        this.tachesAssignees = tachesAssignees;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public boolean isApprouve() {
        return approuve;
    }
    // Items que l'utilisateur vend (devenu seller)
    @OneToMany(mappedBy = "seller")
    @JsonIgnore
    private List<Item> itemsForSale = new ArrayList<>();

    public void setApprouve(boolean approuve) {
        this.approuve = approuve;
    }


    // Transactions en tant qu'acheteur
    @OneToMany(mappedBy = "buyer")
    @JsonIgnore
    private List<Transaction> achats = new ArrayList<>();

    // Transactions en tant que vendeur
    @OneToMany(mappedBy = "seller")
    private List<Transaction> ventes = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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



    public List<Item> getItemsForSale() {
        return itemsForSale;
    }

    public void setItemsForSale(List<Item> itemsForSale) {
        this.itemsForSale = itemsForSale;
    }

    public List<Transaction> getAchats() {
        return achats;
    }

    public void setAchats(List<Transaction> achats) {
        this.achats = achats;
    }

    public List<Transaction> getVentes() {
        return ventes;
    }

    public void setVentes(List<Transaction> ventes) {
        this.ventes = ventes;
    }
}
