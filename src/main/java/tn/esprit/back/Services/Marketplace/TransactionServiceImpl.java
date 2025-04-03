package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Transaction;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Transaction ajouterTransaction(Transaction transaction) {
        // Vérifier et récupérer le buyer depuis la base
        User buyer = userRepository.findByEmail(transaction.getBuyer().getEmail())
                .orElseThrow(() -> new RuntimeException("Acheteur non trouvé : " + transaction.getBuyer().getEmail()));

        // Vérifier et récupérer le seller depuis la base
        User seller = userRepository.findByEmail(transaction.getSeller().getEmail())
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé : " + transaction.getSeller().getEmail()));

        // Vérifier et récupérer l'item depuis la base
        Item item = itemRepository.findById(transaction.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Article non trouvé : ID " + transaction.getItem().getId()));

        // Créer une nouvelle transaction avec les entités existantes
        Transaction newTransaction = new Transaction();
        newTransaction.setBuyer(buyer);
        newTransaction.setSeller(seller);
        newTransaction.setItem(item);
        newTransaction.setTotalAmount(transaction.getTotalAmount());
        newTransaction.setStatus(TransactionStatus.PENDING);
        newTransaction.setCreatedAt(LocalDateTime.now());
        newTransaction.setUpdatedAt(LocalDateTime.now());

        // Sauvegarder la transaction en base
        return transactionRepository.save(newTransaction);
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
    public void supprimerTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByBuyerId(userId);
    }

    @Override
    public Transaction validerTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
        transaction.setValidee(true);
        return transactionRepository.save(transaction);
    }
}
