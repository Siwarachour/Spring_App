package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.back.Entities.User.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // On stocke uniquement l'email de l'utilisateur
    @Column(nullable = false)
    private String userEmail;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;


    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

