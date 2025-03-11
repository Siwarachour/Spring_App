package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Panier;

public interface PanierRepository extends JpaRepository<Panier, Long> {
}
