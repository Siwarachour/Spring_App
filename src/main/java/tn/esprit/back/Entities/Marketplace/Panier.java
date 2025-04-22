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
    private Set<Item> items = new HashSet<>(); // Initialisation ici

    public void calculateTotal() {
        this.total = items.stream()
                .mapToDouble(Item::getPrice)
                .sum();
    }

    public void addItem(Item item) {
        if (items == null) {
            items = new HashSet<>();
        }
        items.add(item);
        calculateTotal();
    }

    public void removeItem(Item item) {
        if (items != null) {
            items.remove(item);
            calculateTotal();
        }
    }
}