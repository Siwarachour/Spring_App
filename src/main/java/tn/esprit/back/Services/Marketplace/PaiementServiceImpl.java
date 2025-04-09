package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Repository.Marketplace.PaiementRepository;
import tn.esprit.back.Repository.Marketplace.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class PaiementServiceImpl implements PaiementService{

    @Autowired
    private PaiementRepository paiementRepository;


    @Override
    public Paiement ajouterPaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement updatePaiement(Paiement paiement) {
        if (paiementRepository.existsById(paiement.getId())) {
            return paiementRepository.save(paiement);
        }
        throw new RuntimeException("Paiement introuvable !");
    }

    @Override
    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    @Override
    public Optional<Paiement> getPaiementById(Long id) {
        return paiementRepository.findById(id);
    }

    @Override
    public void supprimerPaiement(Long id) {
        if (paiementRepository.existsById(id)) {
            paiementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Paiement introuvable !");
        }
    }

    @Autowired
    private TransactionRepository transactionRepository;



    @Override
    public Paiement effectuerPaiement(Long transactionId, PaymentMethod paymentMethod) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction déjà payée ou annulée");
        }

        Paiement paiement = new Paiement();
        paiement.setTransaction(transaction);
        paiement.setPaymentMethod(paymentMethod);
        paiement.setAmount(transaction.getTotalAmount());
        paiement.setPaymentDate(LocalDateTime.now());
        paiement.setPaymentStatus(PaymentStatus.SUCCESS);

        // Mettre à jour la transaction
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return paiementRepository.save(paiement);
    }

}
