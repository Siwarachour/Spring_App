package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Transaction ajouterTransaction(Transaction transaction) {
        // Vérification que l'acheteur et le vendeur existent
        if (transaction.getBuyer() == null || transaction.getSeller() == null) {
            throw new RuntimeException("Acheteur ou vendeur manquant");
        }

        // Vérification que l'article existe
        if (transaction.getItem() == null) {
            throw new RuntimeException("Article manquant");
        }

        // Vérification du statut de l'article
        if (transaction.getItem().getStatus() != ItemStatus.APPROVED) {
            throw new RuntimeException("L'article n'est pas disponible à l'achat");
        }

        // Vérification que l'acheteur n'est pas le vendeur
        if (transaction.getBuyer().getEmail().equals(transaction.getSeller().getEmail())) {
            throw new RuntimeException("L'acheteur ne peut pas être le vendeur");
        }

        // Enregistrement des emails
        transaction.setBuyerEmail(transaction.getBuyer().getEmail());
        transaction.setSellerEmail(transaction.getSeller().getEmail());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }



    @Override
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByBuyerId(userId);
    }

    @Autowired
    private PanierRepository panierRepository;

    @Override
    public Transaction validerTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        // Vérifier que l'article est toujours disponible
        if (transaction.getItem().getStatus() != ItemStatus.APPROVED) {
            throw new RuntimeException("L'article n'est plus disponible");
        }

        // Marquer la transaction comme validée
        transaction.setValidee(true);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setUpdatedAt(LocalDateTime.now());

        // Marquer l'article comme vendu
        Item item = transaction.getItem();
        item.setStatus(ItemStatus.SOLD);
        itemRepository.save(item);

        // Supprimer l'article du panier s'il y est encore
        Optional<Panier> panierOpt = panierRepository.findByUserEmail(transaction.getBuyerEmail());
        panierOpt.ifPresent(panier -> {
            panier.getItems().removeIf(i -> i.getId().equals(item.getId()));
            panier.setTotalPrice(calculerTotalPanier(panier.getId()));
            panierRepository.save(panier);
        });

        return transactionRepository.save(transaction);
    }

    @Override
    public void supprimerTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        // Si la transaction est validée, on ne peut pas la supprimer
        if (transaction.isValidee()) {
            throw new RuntimeException("Impossible de supprimer une transaction validée");
        }

        // Remettre l'article en statut APPROVED s'il était dans cette transaction
        Item item = transaction.getItem();
        if (item != null && item.getStatus() != ItemStatus.SOLD) {
            item.setStatus(ItemStatus.APPROVED);
            itemRepository.save(item);
        }

        transactionRepository.deleteById(id);
    }

    // Ajoutez cette méthode utilitaire si elle n'existe pas
    private BigDecimal calculerTotalPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        return panier.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override
    public List<Transaction> getTransactionsByBuyerEmail(String email) {
        return transactionRepository.findByBuyerEmail(email);
    }
    @Override
    public void annulerTransactionPourItem(Long itemId) {
        Optional<Transaction> transactionOpt = transactionRepository.findByItemIdAndValideeFalse(itemId);
        transactionOpt.ifPresent(transaction -> {
            // Remettre l'article en statut APPROVED
            Item item = transaction.getItem();
            if (item != null) {
                item.setStatus(ItemStatus.APPROVED);
                itemRepository.save(item);
            }
            transactionRepository.delete(transaction);
        });
    }
    @Override
    public List<Transaction> getTransactionsBySellerEmail(String email) {
        return transactionRepository.findBySellerEmail(email);
    }
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
