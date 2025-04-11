package tn.esprit.back.Services.Marketplace;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.ItemStatus;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
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
    @Autowired
    private TransactionRepository transactionRepository;
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            user = userRepository.findByusername(username);
        }

        if (user == null) {
            throw new RuntimeException("User not found with identifier: " + username);
        }
        return user;
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().getName().equals(RoleName.ROLE_ADMIN);
    }

    private boolean isClient(User user) {
        return user.getRole() != null && user.getRole().getName().equals(RoleName.ROLE_CLIENT);
    }

    @Override
    public Item ajouterItem(Item item, Authentication authentication) {
        User user = getCurrentUser();

        // Allow both admins and clients to add items
        if (!isAdmin(user) && !isClient(user)) {
            throw new RuntimeException("Only admins or clients can add items");
        }

        item.setSeller(user);

        // Set different status based on user role
        if (isAdmin(user)) {
            // Admin-added items are automatically approved
            item.setStatus(ItemStatus.APPROVED);
        } else {
            // Client-added items need approval
            item.setStatus(ItemStatus.PENDING);
        }

        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }

    @Override
    public void approveItem(Long itemId) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admin can approve items");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setStatus(ItemStatus.APPROVED);
        itemRepository.save(item);

        // The client becomes seller for this item
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
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + item.getId()));

        // Admin can update any item, clients can only update their own items
        if (!isAdmin(currentUser) &&
                (existingItem.getSeller() == null || existingItem.getSeller().getId() != currentUser.getId())) {
            throw new RuntimeException("You can only update your own items");
        }

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
    @Transactional
    public void supprimerItem(Long id) {
        User currentUser = getCurrentUser();
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));

        // Admin can delete any item, clients can only delete their own items
        if (!isAdmin(currentUser)) {
            if (item.getSeller() == null || item.getSeller().getId() != currentUser.getId()) {
                throw new RuntimeException("You can only delete your own items");
            }
        }

        // First delete all transactions associated with this item
        transactionRepository.deleteByItemId(id);

        // Then delete the item
        itemRepository.delete(item);
    }
    @Override
    public List<Item> getItemsBySeller(int sellerId) {
        User currentUser = getCurrentUser();

        // Admin can view any seller's items, clients can only view their own
        if (!isAdmin(currentUser) && currentUser.getId() != sellerId) {
            throw new RuntimeException("You can only view your own items");
        }

        return itemRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Item> getItemsPendingApproval() {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admin can view pending items");
        }
        return itemRepository.findByStatus(ItemStatus.PENDING);
    }

    @Override
    public void rejectItem(Long itemId) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admin can reject items");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));

        item.setStatus(ItemStatus.REJECTED);
        itemRepository.save(item);
    }
}