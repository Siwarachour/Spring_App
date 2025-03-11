package tn.esprit.back.Services.Marketplace;

import tn.esprit.back.Entities.Marketplace.Panier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PanierService {
    Panier ajouterPanier(Panier panier);
    Panier updatePanier(Panier panier);
    List<Panier> getAllPaniers();
    Optional<Panier> getPanierById(Long id);
    void supprimerPanier(Long id);

    Panier ajouterItemAuPanier(Long panierId, Long itemId);
    Panier supprimerItemDuPanier(Long panierId, Long itemId);
    BigDecimal calculerTotalPanier(Long panierId);
}
