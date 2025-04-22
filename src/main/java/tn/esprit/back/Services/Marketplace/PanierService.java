package tn.esprit.back.Services.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;
import tn.esprit.back.Repository.User.UserRepository;

import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class PanierService {
    private final PanierRepository panierRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Panier getOrCreatePanier(int userId) {
        return panierRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    Panier newPanier = Panier.builder()
                            .user(user)
                            .total(0.0)
                            .build();
                    return panierRepository.save(newPanier);
                });
    }

    @Transactional
    public Panier addItemToPanier(int userId, Long itemId) {
        Panier panier = getOrCreatePanier(userId);
        Item item = itemRepository.findById(itemId).orElseThrow();

        if (!item.isAvailable()) {
            throw new RuntimeException("Cet item n'est pas disponible");
        }

        panier.addItem(item);
        return panierRepository.save(panier);
    }

    @Transactional
    public Panier removeItemFromPanier(int userId, Long itemId) {
        Panier panier = getOrCreatePanier(userId);
        Item item = itemRepository.findById(itemId).orElseThrow();

        panier.removeItem(item);
        return panierRepository.save(panier);
    }

    public Panier getPanier(int userId) {
        return getOrCreatePanier(userId);
    }

    @Transactional
    public void clearPanier(int userId) {
        Panier panier = getOrCreatePanier(userId);
        panier.getItems().clear();
        panier.setTotal(0.0);
        panierRepository.save(panier);
    }
}