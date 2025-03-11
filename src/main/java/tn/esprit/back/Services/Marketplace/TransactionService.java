package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction ajouterTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(Long id);
    void supprimerTransaction(Long id);

    Transaction validerTransaction(Long transactionId);
    List<Transaction> getTransactionsByUser(Long userId);
}
