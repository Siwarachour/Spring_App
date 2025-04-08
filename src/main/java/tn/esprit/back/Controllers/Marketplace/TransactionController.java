package tn.esprit.back.Controllers.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Transaction;
import tn.esprit.back.Services.Marketplace.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Ajouter une nouvelle transaction
    @PostMapping("/add")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction newTransaction = transactionService.ajouterTransaction(transaction);
            return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Mettre à jour une transaction existante
    @PutMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        Optional<Transaction> existingTransaction = transactionService.getTransactionById(id);

        if (existingTransaction.isPresent()) {
            transaction.setId(id); // S'assurer que l'ID de la transaction est correct
            Transaction updatedTransaction = transactionService.updateTransaction(transaction);
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Récupérer toutes les transactions
    @GetMapping("/")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Récupérer une transaction par ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);

        return transaction
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    // Supprimer une transaction par ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);

        if (transaction.isPresent()) {
            transactionService.supprimerTransaction(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Valider une transaction
    @PutMapping("/validate/{id}")
    public ResponseEntity<Transaction> validateTransaction(@PathVariable Long id) {
        try {
            Transaction validatedTransaction = transactionService.validerTransaction(id);
            return new ResponseEntity<>(validatedTransaction, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
