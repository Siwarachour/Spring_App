package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.ItemStatus;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findBySellerId(int sellerId);
    List<Item> findByStatus(ItemStatus status); // Nouvelle méthode pour récupérer les items par statut
}
