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

    public Item addItem(Item item, int userId) {
        User seller = userRepository.findById(userId).orElseThrow();
        item.setSeller(seller);
        item.setStatus(ItemStatus.EN_ATTENTE);
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, int userId) {
        Item existingItem = itemRepository.findById(item.getIdItem()).orElseThrow();
        if (existingItem.getSeller().getId() != userId) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cet item");
        }
        existingItem.setTitle(item.getTitle());
        existingItem.setDescription(item.getDescription());
        existingItem.setPrice(item.getPrice());
        existingItem.setQuantityAvailable(item.getQuantityAvailable());
        existingItem.setCategory(item.getCategory());
        existingItem.setImageUrl(item.getImageUrl());
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
        return itemRepository.findBySellerIdAndStatus(userId, ItemStatus.APPROUVE);
    }

    public List<Item> getPendingItems() {
        return itemRepository.findByStatus(ItemStatus.EN_ATTENTE);
    }

    public Item approveItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setStatus(ItemStatus.APPROUVE);
        return itemRepository.save(item);
    }

    public Item rejectItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        item.setStatus(ItemStatus.REJETE);
        return itemRepository.save(item);
    }

    public List<Item> getAvailableItems() {
        return itemRepository.findByStatus(ItemStatus.APPROUVE);
    }

    public List<Item> getItemsByCategory(ItemCategory category) {
        return itemRepository.findByStatusAndCategory(ItemStatus.APPROUVE, category);
    }
}