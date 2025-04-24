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

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Stripe-specific fields
    private String stripePaymentIntentId;
    private String stripeClientSecret;
    private String stripeCurrency = "eur"; // Par défaut en euros

    // Méthode de paiement (peut être null avant paiement)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Date datePaiement;
    private String paymentReceiptUrl;

    public enum PaymentStatus {
        PENDING,    // En attente de paiement
        SUCCEEDED,  // Paiement réussi
        FAILED,     // Paiement échoué
        CANCELED    // Paiement annulé
    }

    // Méthode pour générer une facture à partir d'un panier
    public static Facture fromPanier(Panier panier) {
        if (panier == null) {
            throw new IllegalArgumentException("Panier ne peut pas être null");
        }
        if (panier.getUser() == null) {
            throw new IllegalArgumentException("Le panier doit avoir un utilisateur");
        }
        if (panier.getItems() == null) {
            throw new IllegalArgumentException("Les items du panier ne peuvent pas être null");
        }

        // Vérifiez que le calcul du total est correct
        double total = panier.getItems().stream()
                .mapToDouble(Item::getPrice)
                .sum();

        return Facture.builder()
                .client(panier.getUser())
                .items(new HashSet<>(panier.getItems())) // Copie défensive
                .montantTotal(total)
                .dateCreation(new Date())
                .status(PaymentStatus.PENDING)
                .stripeCurrency("eur") // Défaut EUR
                .build();
    }

    // Méthode pour marquer la facture comme payée
    public void markAsPaid(String paymentIntentId, PaymentMethod method) {
        this.status = PaymentStatus.SUCCEEDED;
        this.stripePaymentIntentId = paymentIntentId;
        this.paymentMethod = method;
        this.datePaiement = new Date();
    }

    // Méthode pour vérifier si la facture est payée
    public boolean isPayee() {
        return this.status == PaymentStatus.SUCCEEDED;
    }
}