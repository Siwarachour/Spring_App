package tn.esprit.back.Services.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
public class PanierServiceImpl implements PanierService {
    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Panier ajouterPanier(Panier panier) {
        return panierRepository.save(panier);
    }

    @Override
    public Panier updatePanier(Panier panier) {
        return panierRepository.save(panier);
    }

    @Override
    public List<Panier> getAllPaniers() {
        return panierRepository.findAll();
    }

    @Override
    public Optional<Panier> getPanierById(Long id) {
        return panierRepository.findById(id);
    }

    @Override
    public void supprimerPanier(Long id) {
        panierRepository.deleteById(id);
    }

    @Override
    public Panier ajouterItemAuPanier(Long panierId, Long itemId) {
        Panier panier = panierRepository.findById(panierId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        panier.getItems().add(item);
        return panierRepository.save(panier);
    }

    @Override
    public Panier supprimerItemDuPanier(Long panierId, Long itemId) {
        Panier panier = panierRepository.findById(panierId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        panier.getItems().remove(item);
        return panierRepository.save(panier);
    }

    @Override
    public BigDecimal calculerTotalPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId).orElseThrow();
        return panier.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Somme avec BigDecimal
    }


}