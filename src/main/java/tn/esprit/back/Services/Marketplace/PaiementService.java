package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Paiement;

import java.util.List;
import java.util.Optional;

public interface PaiementService {
    Paiement ajouterPaiement(Paiement paiement);
    Paiement updatePaiement(Paiement paiement);
    List<Paiement> getAllPaiements();
    Optional<Paiement> getPaiementById(Long id);
    void supprimerPaiement(Long id);

    Paiement effectuerPaiement(Long transactionId, String modePaiement);
}
