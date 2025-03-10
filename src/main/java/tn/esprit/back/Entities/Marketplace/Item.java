package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.back.Entities.User.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "item")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private ItemCategory category;
    @ElementCollection
    private List<String> images;  // Liste des URLs des images
    private ItemStatus status;  // Enum pour l'état de l'article (AVAILABLE, SOLD)

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;  // Relation Many-to-One avec l'utilisateur (vendeur)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)

    private Date updatedAt;

}
