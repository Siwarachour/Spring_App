package tn.esprit.back.Entities.User;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Transaction;
import tn.esprit.back.Entities.Role.Role;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Data
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
    private boolean enabled;
    private boolean accountLocked;
    //private String role;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Role> roles;
    // Liste des articles mis en vente par l'utilisateur
    @OneToMany(mappedBy = "seller")
    private List<Item> itemsForSale;

    // Liste des transactions effectuées par l'utilisateur en tant qu'acheteur
    @OneToMany(mappedBy = "buyer")
    private List<Transaction> purchases;

/*
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastmodifiedDate;*/

}
