package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    Item ajouterItem(Item item);
    Item updateItem(Item item);
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    void supprimerItem(Long id);

    List<Item> getItemsBySeller(Long sellerId);

}
