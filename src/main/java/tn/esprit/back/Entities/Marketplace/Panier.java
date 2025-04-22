package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.back.Entities.User.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPanier;
    private double total;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "panier_items",
            joinColumns = @JoinColumn(name = "panier_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
            private Set<Item> items = new HashSet<>();


            // Méthode pour calculer le total
            public void calculateTotal() {
            this.total = items.stream()
            .mapToDouble(Item::getPrice)
            .sum();
            }

            // Méthode pour ajouter un item
            public void addItem(Item item) {
        items.add(item);
        calculateTotal();
    }

    // Méthode pour supprimer un item
    public void removeItem(Item item) {
        items.remove(item);
        calculateTotal();
    }
}