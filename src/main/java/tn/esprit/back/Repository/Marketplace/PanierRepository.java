package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Panier;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUserId(int userId); // Retourne un Optional<Panier>
}
