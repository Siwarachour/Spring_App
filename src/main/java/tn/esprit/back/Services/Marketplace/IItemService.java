package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    // Retirer l'email comme paramètre ici et le gérer dans la méthode d'implémentation
    Item ajouterItem(Item item);  // Ne prend plus userEmail en paramètre
    Item updateItem(Item item);
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    void supprimerItem(Long id);

    List<Item> getItemsBySeller(int sellerId);
}
