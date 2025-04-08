package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findBySellerId(int sellerId);
}
