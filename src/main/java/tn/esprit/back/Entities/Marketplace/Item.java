package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.back.Entities.User.User;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    private String title;
    private String description;
    private double price;
    private int quantityAvailable;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;

    // Méthode pour vérifier si l'item est disponible
    public boolean isAvailable() {
        return quantityAvailable > 0 && status == ItemStatus.APPROUVE;
    }
}