package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
