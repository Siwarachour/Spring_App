package tn.esprit.back.Services.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Repository.Marketplace.FactureRepository;
import tn.esprit.back.Repository.Marketplace.ItemRepository;
import tn.esprit.back.Repository.Marketplace.PaiementRepository;
import tn.esprit.back.Repository.Marketplace.PanierRepository;

import org.springframework.transaction.annotation.Transactional;import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PanierRepository panierRepository;
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    public Facture createFactureFromPanier(int userId) {
        Panier panier = panierRepository.findByUserId(userId).orElseThrow();

        if (panier.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        // Vérifier la disponibilité des items
        panier.getItems().forEach(item -> {
            if (!item.isAvailable()) {
                throw new RuntimeException("L'item " + item.getTitle() + " n'est plus disponible");
            }
        });

        // Créer la facture
        Facture facture = Facture.fromPanier(panier);
        facture = factureRepository.save(facture);

        // Vider le panier
        panier.getItems().clear();
        panier.setTotal(0.0);
        panierRepository.save(panier);

        return facture;
    }

    @Transactional
    public Paiement processPayment(Long factureId, PaymentMethod methode, String reference) {
        Facture facture = factureRepository.findById(factureId).orElseThrow();

        if (facture.isPayee()) {
            throw new RuntimeException("Cette facture a déjà été payée");
        }

        // Mettre à jour les quantités des items
        facture.getItems().forEach(item -> {
            item.setQuantityAvailable(item.getQuantityAvailable() - 1);
            itemRepository.save(item);
        });

        // Créer le paiement
        Paiement paiement = Paiement.builder()
                .facture(facture)
                .methode(methode)
                .montant(facture.getMontantTotal())
                .datePaiement(new Date())
                .reference(reference)
                .estValide(true)
                .build();

        // Valider le paiement et la facture
        paiement.validerPaiement();

        paiement = paiementRepository.save(paiement);
        factureRepository.save(facture);

        return paiement;
    }

    public List<Facture> getUserFactures(int userId) {
        return factureRepository.findByClientId(userId);
    }

    public Optional<Paiement> getPaymentByFacture(Long factureId) {
        return paiementRepository.findByFacture_IdFacture(factureId);
    }}