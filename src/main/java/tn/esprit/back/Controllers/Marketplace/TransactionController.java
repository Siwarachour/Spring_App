package tn.esprit.back.Controllers.Marketplace;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    @Operation(summary = "Ajouter une transaction")
    public Transaction ajouterTransaction(@RequestBody Transaction transaction) {
        return transactionService.ajouterTransaction(transaction);
    }

    @PutMapping
    @Operation(summary = "Mettre à jour une transaction")
    public Transaction updateTransaction(@RequestBody Transaction transaction) {
        return transactionService.updateTransaction(transaction);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une transaction par son ID")
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une transaction par son ID")
    public void supprimerTransaction(@PathVariable Long id) {
        transactionService.supprimerTransaction(id);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les transactions d'un utilisateur")
    public List<Transaction> getTransactionsByUser(@PathVariable Long userId) {
        return transactionService.getTransactionsByUser(userId);
    }

    @PutMapping("/valider/{transactionId}")
    @Operation(summary = "Valider une transaction")
    public Transaction validerTransaction(@PathVariable Long transactionId) {
        return transactionService.validerTransaction(transactionId);
    }
}
