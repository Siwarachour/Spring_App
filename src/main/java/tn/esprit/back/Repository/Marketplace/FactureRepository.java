package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Facture;

import java.util.List;

public interface FactureRepository  extends JpaRepository<Facture, Long> {
    List<Facture> findByClientId(int clientId);
}
