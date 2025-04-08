package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ElementCollection
    private List<String> images;  // Liste des URLs ou chemins des images

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;  // L'utilisateur qui vend l'article

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur personnalisé pour accepter l'email du vendeur
    public Item(String title, String description, BigDecimal price, Category category,
                List<String> images, String sellerEmail, ItemStatus status, UserRepository userRepository) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.images = images;
        this.status = status;

        // Récupérer l'utilisateur vendeur à partir de l'email
        User seller = userRepository.findByEmail(sellerEmail);
        if (seller != null) {
            this.seller = seller;
        } else {
            throw new RuntimeException("Vendeur non trouvé avec l'email : " + sellerEmail);
        }

        this.createdAt = LocalDateTime.now();  // Initialisation de la date de création
        this.updatedAt = LocalDateTime.now();  // Initialisation de la date de mise à jour
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // Initialisation de la date de création
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();  // Mise à jour de la date de mise à jour
    }
}
