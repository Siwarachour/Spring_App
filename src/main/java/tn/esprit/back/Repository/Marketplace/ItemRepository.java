package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.ItemCategory;
import tn.esprit.back.Entities.Marketplace.ItemStatus;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByStatus(ItemStatus status);

    List<Item> findBySellerIdAndStatus(int sellerId, ItemStatus status);
    List<Item> findByCategory(ItemCategory category);
    List<Item> findByStatusAndCategory(ItemStatus status, ItemCategory category);

}
