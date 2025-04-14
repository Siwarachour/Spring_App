package tn.esprit.back.Services.Marketplace;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userByEmail = Optional.ofNullable(userRepository.findByEmail(username));
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }
        Optional<User> userByUsername = Optional.ofNullable(userRepository.findByusername(username));
        return userByUsername.orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private boolean hasAdminRole(User user) {
        return user.getRole() != null && user.getRole().getName() == RoleName.ROLE_ADMIN;
    }

    private boolean hasClientRole(User user) {
        return user.getRole() != null && user.getRole().getName() == RoleName.ROLE_CLIENT;
    }

    @Override
    public Item ajouterItem(Item item, MultipartFile[] files, Long sellerId) {
        User currentUser = getCurrentUser();

        // Vérification des permissions
        if (!hasClientRole(currentUser)) {
            throw new SecurityException("Seuls les clients peuvent ajouter des items");
        }

        if (currentUser.getId() != sellerId) {
            throw new SecurityException("Vous ne pouvez créer que vos propres items");
        }

        // Traitement des fichiers
        if (files != null && files.length > 0) {
            try {
                for (MultipartFile file : files) {
                    // Stocker les fichiers et ajouter les URLs à l'item
                    String fileUrl = storeFile(file); // Implémentez cette méthode
                    item.getImages().add(fileUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du traitement des fichiers", e);
            }
        }

        // Configuration de l'item
        item.setSeller(currentUser);
        item.setStatus(ItemStatus.PENDING);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item, MultipartFile[] files, Long sellerId) {
        User currentUser = getCurrentUser();
        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));

        // Vérification des permissions
        if (!hasClientRole(currentUser) ||
                existingItem.getSeller().getId() != currentUser.getId() ||
                currentUser.getId() != sellerId) {
            throw new SecurityException("Action non autorisée");
        }

        // Traitement des fichiers
        if (files != null && files.length > 0) {
            existingItem.getImages().clear();
            try {
                for (MultipartFile file : files) {
                    String fileUrl = storeFile(file);
                    existingItem.getImages().add(fileUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la mise à jour des fichiers", e);
            }
        }

        // Mise à jour des champs
        existingItem.setTitle(item.getTitle());
        existingItem.setDescription(item.getDescription());
        existingItem.setPrice(item.getPrice());
        existingItem.setCategory(item.getCategory());
        existingItem.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void supprimerItem(Long itemId, Long sellerId) {
        User currentUser = getCurrentUser();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));

        // Vérification des permissions
        if (!hasClientRole(currentUser) ||
                item.getSeller().getId() != currentUser.getId() ||
                currentUser.getId() != sellerId) {
            throw new SecurityException("Action non autorisée");
        }

        // Suppression des transactions associées
        transactionRepository.deleteByItemId(itemId);

        // Suppression de l'item
        itemRepository.delete(item);
    }

    @Override
    public List<Item> getItemsBySeller(Long sellerId) {
        User currentUser = getCurrentUser();

        // Un client ne peut voir que ses propres items
        if (hasClientRole(currentUser) && currentUser.getId() != sellerId) {
            throw new SecurityException("Action non autorisée");
        }

        return itemRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Item> getItemsPendingApproval() {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new SecurityException("Seuls les admins peuvent voir les items en attente");
        }
        return itemRepository.findByStatus(ItemStatus.PENDING);
    }

    @Override
    public void approveItem(Long itemId) {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new SecurityException("Seuls les admins peuvent approuver des items");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));

        item.setStatus(ItemStatus.APPROVED);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    public void rejectItem(Long itemId) {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new SecurityException("Seuls les admins peuvent rejeter des items");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));

        item.setStatus(ItemStatus.REJECTED);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        User currentUser = getCurrentUser();

        // Les admins voient tous les items
        if (hasAdminRole(currentUser)) {
            return itemRepository.findAll();
        }

        // Les clients ne voient que les items approuvés
        return itemRepository.findByStatus(ItemStatus.APPROVED);
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        User currentUser = getCurrentUser();
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            // Les admins peuvent tout voir
            if (hasAdminRole(currentUser)) {
                return item;
            }

            // Les clients ne peuvent voir que leurs items ou les items approuvés
            boolean isOwner = item.get().getSeller() != null &&
                    item.get().getSeller().getId() == currentUser.getId();
            boolean isApproved = item.get().getStatus() == ItemStatus.APPROVED;

            if (!isOwner && !isApproved) {
                throw new SecurityException("Action non autorisée");
            }
        }

        return item;
    }

    private String storeFile(MultipartFile file) throws IOException {
        // Implémentez le stockage physique des fichiers
        // Retournez l'URL du fichier stocké
        return "url_du_fichier_stocké";
    }
}