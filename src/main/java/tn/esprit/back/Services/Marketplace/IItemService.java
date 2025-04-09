package tn.esprit.back.Services.Marketplace;

import org.springframework.security.core.Authentication;
import tn.esprit.back.Entities.Marketplace.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    Item ajouterItem(Item item, Authentication authentication);  // Ajouter le paramètre Authentication

    Item updateItem(Item item);
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    void supprimerItem(Long id);

    List<Item> getItemsBySeller(int sellerId);
    List<Item> getItemsPendingApproval();
    void approveItem(Long itemId);
    void rejectItem(Long itemId);
}
