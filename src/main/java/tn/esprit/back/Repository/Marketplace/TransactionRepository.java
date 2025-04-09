package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Marketplace.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBuyerId(Long userId);
    List<Transaction> findByBuyerEmail(String buyerEmail);
    List<Transaction> findBySellerEmail(String sellerEmail);
    Optional<Transaction> findByItemIdAndValideeFalse(Long itemId);

}
