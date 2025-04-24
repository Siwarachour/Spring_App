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

    @OneToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    private String stripePaymentIntentId;
    private String stripeChargeId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod methode;

    private double montant;
    private Date datePaiement;
    private String reference; // Référence de transaction
    public enum PaymentStatus {
        REQUIRES_PAYMENT_METHOD,
        REQUIRES_CONFIRMATION,
        REQUIRES_ACTION,
        PROCESSING,
        REQUIRES_CAPTURE,
        CANCELED,
        SUCCEEDED
    }
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String receiptUrl;
    private String billingDetails; // JSON des détails de facturation

    public void validerPaiement() {
        this.status = PaymentStatus.SUCCEEDED;
        this.facture.markAsPaid(this.stripePaymentIntentId, this.methode);
    }
}