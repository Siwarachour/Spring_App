package tn.esprit.back.Services.Marketplace;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Marketplace.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {

    // Méthodes pour les clients
    Item ajouterItem(Item item, MultipartFile[] files, Long sellerId);
    Item updateItem(Item item, MultipartFile[] files, Long sellerId);
    void supprimerItem(Long idItem, Long sellerId);
    List<Item> getItemsBySeller(Long sellerId);

    // Méthodes pour les administrateurs
    List<Item> getItemsPendingApproval();
    void approveItem(Long idItem);
    void rejectItem(Long idItem);

    // Méthodes publiques
    List<Item> getAllItems();
    Optional<Item> getItemById(Long idItem);

    // Méthodes utilitaires
}