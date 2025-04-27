package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Facture;

import java.util.List;
import java.util.Optional;

public interface FactureRepository  extends JpaRepository<Facture, Long> {
    List<Facture> findByClientId(int clientId);
    Optional<Facture> findByStripePaymentIntentId(String stripePaymentIntentId);
    boolean existsByStripePaymentIntentId(String stripePaymentIntentId);
    List<Facture> findAllByClientId(int clientId);

}
