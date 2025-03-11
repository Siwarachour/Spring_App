package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Paiement;
import tn.esprit.back.Entities.Marketplace.PaymentMethod;
import tn.esprit.back.Entities.Marketplace.PaymentStatus;
import tn.esprit.back.Repository.Marketplace.PaiementRepository;

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

    @Override
    public Paiement effectuerPaiement(Long transactionId, String modePaiement) {
        Optional<Paiement> paiementOptional = paiementRepository.findById(transactionId);
        if (paiementOptional.isPresent()) {
            Paiement paiement = paiementOptional.get();

            // Convertir modePaiement en enum PaymentMethod
            try {
                PaymentMethod paymentMethod = PaymentMethod.valueOf(modePaiement.toUpperCase());
                paiement.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Mode de paiement invalide : " + modePaiement);
            }

            paiement.setPaymentStatus(PaymentStatus.SUCCESS);
            paiement.setPaymentDate(LocalDateTime.now());

            return paiementRepository.save(paiement);
        } else {
            throw new RuntimeException("Transaction introuvable !");
        }
    }

}
