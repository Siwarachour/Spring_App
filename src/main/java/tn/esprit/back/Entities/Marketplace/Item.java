package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.back.Entities.User.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

// ENUM POUR CATEGORY
enum Category {
    BOOKS, PROJECTS, RESOURCES, ELECTRONICS, OTHERS;
}

// ENTITY : ITEM (ARTICLE)
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
    private List<String> images;


    @ManyToOne
    @JoinColumn(name = "seller_email", referencedColumnName = "email", nullable = true)
    private User seller;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// ENUM POUR STATUS DE L'ITEM
enum ItemStatus {
    AVAILABLE, SOLD, UNAVAILABLE;
}