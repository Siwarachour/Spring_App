package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Entities.User.User;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUserEmail(String userEmail);
    Optional<Panier> findByUser(User user);

}
