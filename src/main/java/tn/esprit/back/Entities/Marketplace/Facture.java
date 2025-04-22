package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.back.Entities.User.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFacture;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User client;

    @ManyToMany
    @JoinTable(
            name = "facture_items",
            joinColumns = @JoinColumn(name = "facture_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<Item> items = new HashSet<>();

    private double montantTotal;
    private Date dateCreation;
    private boolean payee = false;

    // Méthode pour générer une facture à partir d'un panier
    public static Facture fromPanier(Panier panier) {
        Facture facture = new Facture();
        facture.setClient(panier.getUser());
        facture.setItems(new HashSet<>(panier.getItems()));
        facture.setMontantTotal(panier.getTotal());
        facture.setDateCreation(new Date());
        return facture;
    }
}