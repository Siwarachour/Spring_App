package tn.esprit.back.Entities.Marketplace;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.back.Entities.User.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_email", referencedColumnName = "email", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_email", referencedColumnName = "email", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setValidee(boolean b) {
    }
}

// ENUM POUR STATUS DE TRANSACTION
enum TransactionStatus {
    PENDING, COMPLETED, FAILED;
}
