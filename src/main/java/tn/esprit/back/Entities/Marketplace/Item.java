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
    @Lob // Pour les grandes données (Base64)
    @Column(columnDefinition = "LONGTEXT") // Pour MySQL
    private String imageData; // Stockage direct des données d'image en Base64

    private String imageType; // Type MIME de l'image (ex: "image/jpeg")
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // Méthodes pour gérer l'image
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    // Méthode utilitaire pour obtenir l'URL de l'image
    public String getImageUrl() {
        if (imageData != null && imageType != null) {
            return "data:" + imageType + ";base64," + imageData;
        }
        return null;
    }

    // Méthode pour vérifier si l'item est disponible
    public boolean isAvailable() {
        return quantityAvailable > 0 && status == ItemStatus.APPROVED;
    }
}