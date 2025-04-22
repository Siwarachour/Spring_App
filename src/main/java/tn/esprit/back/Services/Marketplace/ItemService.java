package tn.esprit.back.Services.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Add this method to get an item by ID
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    public Item addItem(Item item, int userId) {
        User seller = userRepository.findById(userId).orElseThrow();
        item.setSeller(seller);
        item.setStatus(ItemStatus.PENDING);
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, int userId) {
        Item existingItem = itemRepository.findById(item.getIdItem())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (existingItem.getSeller().getId() != userId) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cet item");
        }

        existingItem.setTitle(item.getTitle());
        existingItem.setDescription(item.getDescription());
        existingItem.setPrice(item.getPrice());
        existingItem.setQuantityAvailable(item.getQuantityAvailable());
        existingItem.setCategory(item.getCategory());

        // Mettez à jour les données d'image seulement si elles sont fournies
        if (item.getImageData() != null && !item.getImageData().isEmpty()) {
            existingItem.setImageData(item.getImageData());
            existingItem.setImageType(item.getImageType());
        }

        return itemRepository.save(existingItem);
    }
    public void deleteItem(Long itemId, int userId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (item.getSeller().getId() != userId) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cet item");
        }
        itemRepository.delete(item);
    }

    public List<Item> getItemsBySeller(int userId) {
        return itemRepository.findBySellerIdAndStatus(userId, ItemStatus.APPROVED);
    }
    public List<Item> getItemsBySellerAndStatus(int sellerId, ItemStatus status) {
        return itemRepository.findBySellerIdAndStatus(sellerId, status);
    }
    public List<Item> getPendingItems() {
        return itemRepository.findByStatus(ItemStatus.PENDING);
    }

    public Item approveItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setStatus(ItemStatus.APPROVED);
        return itemRepository.save(item);
    }

    public Item rejectItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setStatus(ItemStatus.REJECTED);
        return itemRepository.save(item);
    }

    public List<Item> getAvailableItems() {
        return itemRepository.findByStatus(ItemStatus.APPROVED);
    }

    public List<Item> getItemsByCategory(ItemCategory category) {
        return itemRepository.findByStatusAndCategory(ItemStatus.APPROVED, category);
    }
}