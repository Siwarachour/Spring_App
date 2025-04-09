package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.ItemStatus;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            user = userRepository.findByusername(username);
        }

        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable avec l'identifiant : " + username);
        }
        return user;
    }

    @Override
    public Item ajouterItem(Item item, Authentication authentication) {
        // Récupérer le nom d'utilisateur ou email du principal
        String principalName = authentication.getName();

        // Essayer de trouver l'utilisateur par email d'abord
        User user = userRepository.findByEmail(principalName);

        // Si non trouvé, essayer par username
        if (user == null) {
            user = userRepository.findByusername(principalName); // Notez le changement de findByusername à findByUsername
        }

        // Si toujours non trouvé, lever une exception
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé avec l'identifiant : " + principalName);
        }

        // Vérifier le rôle
        if (user.getRole() == null || !user.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new RuntimeException("Seuls les clients peuvent ajouter des articles");
        }

        // Configurer l'item
        item.setSeller(user);
        item.setStatus(ItemStatus.PENDING);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }
    @Override
    public void approveItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        item.setStatus(ItemStatus.APPROVED);
        itemRepository.save(item);

        // Le client devient automatiquement seller pour cet item
        User seller = item.getSeller();
        if (seller != null) {
            if (seller.getItemsForSale() == null) {
                seller.setItemsForSale(new ArrayList<>());
            }
            seller.getItemsForSale().add(item);
            userRepository.save(seller);
        }
    }

    @Override
    public Item updateItem(Item item) {
        User currentUser = getCurrentUser();
        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID : " + item.getId()));

        // Vérification si l'utilisateur est le vendeur de l'article original
        if (existingItem.getSeller() == null || existingItem.getSeller().getId() != currentUser.getId()) {
            throw new RuntimeException("Vous ne pouvez pas modifier un article qui ne vous appartient pas.");
        }

        // Mise à jour uniquement des champs modifiables
        existingItem.setTitle(item.getTitle());
        existingItem.setDescription(item.getDescription());
        existingItem.setPrice(item.getPrice());
        existingItem.setCategory(item.getCategory());
        existingItem.setImages(item.getImages());
        existingItem.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(existingItem);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public void supprimerItem(Long id) {
        User currentUser = getCurrentUser();
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID : " + id));

        if (item.getSeller() == null || item.getSeller().getId() != currentUser.getId()) {
            throw new RuntimeException("Vous ne pouvez pas supprimer un article qui ne vous appartient pas.");
        }

        itemRepository.delete(item);
    }

    // ... (autres méthodes inchangées)


    @Override
    public List<Item> getItemsBySeller(int sellerId) {
        return itemRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Item> getItemsPendingApproval() {
        return itemRepository.findByStatus(ItemStatus.PENDING);
    }



    @Override
    public void rejectItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID : " + itemId));

        item.setStatus(ItemStatus.REJECTED);
        itemRepository.save(item);
    }
}