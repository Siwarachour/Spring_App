package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Entities.Marketplace.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PanierService {
    Panier ajouterPanier(Panier panier);
    Panier getOrCreatePanierForCurrentUser();
    List<Panier> getAllPaniers();
    Optional<Panier> getPanierById(Long id);
    void supprimerPanier(Long id);

    Panier ajouterItemAuPanier(Long itemId);

    Transaction validerAchat(Long panierId);

    Panier supprimerItemDuPanier(Long itemId);
    BigDecimal calculerTotalPanier(Long panierId);
}
