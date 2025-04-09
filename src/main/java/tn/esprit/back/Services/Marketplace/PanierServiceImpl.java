package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class PanierServiceImpl implements PanierService {

    @Autowired
    private PanierRepository panierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;
    @Override
    public Panier getOrCreatePanierForCurrentUser() {
        String username = getCurrentUsername();
        User user = userRepository.findByusername(username); // Retourne User directement

        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Optional<Panier> existingPanier = panierRepository.findByUser(user);

        if (existingPanier.isPresent()) {
            return existingPanier.get();
        } else {
            Panier newPanier = new Panier();
            newPanier.setUser(user);
            newPanier.setUserEmail(user.getEmail());
            newPanier.setItems(new ArrayList<>());
            return panierRepository.save(newPanier);
        }
    }
    @Override
    public Panier ajouterItemAuPanier(Long itemId) {
        // 1. Récupérer l'utilisateur courant
        String username = getCurrentUsername();
        User buyer = userRepository.findByusername(username);
        if (buyer == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // 2. Récupérer ou créer le panier de l'utilisateur
        Panier panier = panierRepository.findByUser(buyer)
                .orElseGet(() -> {
                    Panier newPanier = new Panier();
                    newPanier.setUser(buyer);
                    newPanier.setUserEmail(buyer.getEmail());
                    newPanier.setItems(new ArrayList<>());
                    return panierRepository.save(newPanier);
                });

        // 3. Récupérer l'item à ajouter
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

//        // 4. Vérifier que l'article est disponible
//        if (item.getStatus() != ItemStatus.APPROVED) {
//            throw new RuntimeException("L'article n'est pas disponible pour l'achat");
//        }

        // 5. Vérifier que l'article n'est pas déjà dans le panier
        if (panier.getItems().stream().anyMatch(i -> i.getId().equals(itemId))) {
            throw new RuntimeException("L'article est déjà dans le panier");
        }

        // 6. Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setBuyer(buyer);
        transaction.setSeller(item.getSeller());
        transaction.setItem(item);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTotalAmount(item.getPrice());

        // Sauvegarder la transaction
        transactionRepository.save(transaction);

        // 7. Ajouter l'article au panier
        panier.getItems().add(item);
        item.setPanier(panier);

        // 8. Mettre à jour le prix total
        panier.setTotalPrice(calculerTotalPanier(panier.getId()));

        // 9. Sauvegarder les modifications
        itemRepository.save(item);
        return panierRepository.save(panier);
    }

    @Override
    public Transaction validerAchat(Long panierId) {
        String buyerEmail = getCurrentUserEmail();
        User buyer = userRepository.findByEmail(buyerEmail); // Retourne User directement (peut être null)

        if (buyer == null) {
            throw new RuntimeException("Acheteur non trouvé");
        }

        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        if (!panier.getUserEmail().equals(buyerEmail)) {
            throw new RuntimeException("Ce panier ne vous appartient pas");
        }


        // Créer la transaction
        Transaction transaction = new Transaction();
        transaction.setBuyer(buyer);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Item item : panier.getItems()) {
            // Vérifier que chaque item est toujours disponible
            if (item.getStatus() != ItemStatus.APPROVED) {
                throw new RuntimeException("L'article " + item.getTitle() + " n'est plus disponible");
            }

            // Ajouter à la transaction
            totalAmount = totalAmount.add(item.getPrice());
            item.setStatus(ItemStatus.SOLD);

            // Enregistrer le vendeur pour le premier item
            if (transaction.getSeller() == null) {
                transaction.setSeller(item.getSeller());
            }
        }

        transaction.setTotalAmount(totalAmount);
        transactionRepository.save(transaction);

        // Vider le panier
        panier.getItems().clear();
        panier.setTotalPrice(BigDecimal.ZERO);
        panierRepository.save(panier);

        return transaction;
    }

    @Override
    public Panier supprimerItemDuPanier(Long itemId) {
        String userEmail = getCurrentUserEmail();
        Panier panier = panierRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        Item item = panier.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item non trouvé dans le panier"));

        // Trouver et annuler la transaction associée à cet item
        Optional<Transaction> transactionOpt = transactionRepository.findByItemIdAndValideeFalse(itemId);
        transactionOpt.ifPresent(transaction -> {
            transactionRepository.delete(transaction);
        });

        panier.getItems().remove(item);
        item.setPanier(null);
        panier.setTotalPrice(calculerTotalPanier(panier.getId()));

        return panierRepository.save(panier);
    }
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            User user = userRepository.findByusername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            return user.getEmail();
        }
        throw new RuntimeException("User not authenticated");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    @Override
    public List<Panier> getAllPaniers() {
        return panierRepository.findAll();
    }

    @Override
    public Optional<Panier> getPanierById(Long id) {
        return panierRepository.findById(id);
    }

    @Override
    public void supprimerPanier(Long id) {
        panierRepository.deleteById(id);
    }




    @Override
    public Panier ajouterPanier(Panier panier) {
        return panierRepository.save(panier);
    }


    @Override
    public BigDecimal calculerTotalPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        return panier.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Vous pouvez supprimer getCurrentUserEmail() car elle est remplacée par getCurrentUser()
}

