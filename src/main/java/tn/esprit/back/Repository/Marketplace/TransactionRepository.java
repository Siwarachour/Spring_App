package tn.esprit.back.Repository.Marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.back.Entities.Marketplace.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBuyerId(Long userId);
    List<Transaction> findByBuyerEmail(String buyerEmail);
    List<Transaction> findBySellerEmail(String sellerEmail);
    Optional<Transaction> findByItemIdAndValideeFalse(Long itemId);
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);
}
