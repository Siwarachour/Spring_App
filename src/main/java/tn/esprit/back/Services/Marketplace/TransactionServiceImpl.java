package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Transaction;
import tn.esprit.back.Entities.Marketplace.TransactionStatus;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.Role.RoleName;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;
import tn.esprit.back.Repository.User.UserRepository;

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
        // Récupérer l'email de l'utilisateur connecté
        String userEmail = getCurrentUserEmail();

        // Récupérer l'utilisateur par son email
        User buyer = userRepository.findByEmail(userEmail);
        if (buyer == null) {
            throw new RuntimeException("Acheteur non trouvé avec l'email : " + userEmail);
        }

        // Vérifier que l'utilisateur a bien le rôle 'ROLE_CLIENT' et l'associer comme acheteur
        if (buyer.getRole().getName() == RoleName.ROLE_CLIENT) {
            // Vérifier que le vendeur et l'article existent
            User seller = userRepository.findByEmail(transaction.getSeller().getEmail());
            if (seller == null) {
                throw new RuntimeException("Vendeur non trouvé : " + transaction.getSeller().getEmail());
            }

            Item item = itemRepository.findById(transaction.getItem().getId())
                    .orElseThrow(() -> new RuntimeException("Article non trouvé : ID " + transaction.getItem().getId()));

            // Créer la transaction
            Transaction newTransaction = new Transaction();
            newTransaction.setBuyer(buyer); // Associer l'acheteur
            newTransaction.setSeller(seller); // Associer le vendeur
            newTransaction.setItem(item);
            newTransaction.setTotalAmount(transaction.getTotalAmount());
            newTransaction.setStatus(TransactionStatus.PENDING);
            newTransaction.setCreatedAt(LocalDateTime.now());
            newTransaction.setUpdatedAt(LocalDateTime.now());

            // Sauvegarder la transaction
            return transactionRepository.save(newTransaction);
        } else {
            throw new RuntimeException("Seul un client peut effectuer une transaction !");
        }
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

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
