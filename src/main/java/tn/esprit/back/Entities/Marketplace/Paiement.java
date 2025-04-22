package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    @ManyToOne
    @JoinColumn(name = "facture_id") // Assurez-vous que cette colonne existe
    private Facture facture;

    @Enumerated(EnumType.STRING)
    private PaymentMethod methode;

    private double montant;
    private Date datePaiement;
    private String reference;
    private boolean estValide = false;

    // Méthode pour valider un paiement
    public void validerPaiement() {
        this.estValide = true;
        this.facture.setPayee(true);
    }
}