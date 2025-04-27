package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Paiement;

import java.util.List;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByFacture_IdFacture(Long factureId);
    Optional<Paiement> findByStripePaymentIntentId(String stripePaymentIntentId);
        List<Paiement> findAllByFactureClientId(int clientId);
    }

