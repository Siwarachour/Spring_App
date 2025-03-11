package tn.esprit.back.Controllers.Marketplace;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Services.Marketplace.PanierService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paniers")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @PostMapping
    @Operation(summary = "Ajouter un panier")
    public Panier ajouterPanier(@RequestBody Panier panier) {
        return panierService.ajouterPanier(panier);
    }

    @PutMapping
    @Operation(summary = "Mettre à jour un panier")
    public Panier updatePanier(@RequestBody Panier panier) {
        return panierService.updatePanier(panier);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les paniers")
    public List<Panier> getAllPaniers() {
        return panierService.getAllPaniers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un panier par son ID")
    public Optional<Panier> getPanierById(@PathVariable Long id) {
        return panierService.getPanierById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un panier par son ID")
    public void supprimerPanier(@PathVariable Long id) {
        panierService.supprimerPanier(id);
    }

    @PutMapping("/{panierId}/ajouter-item/{itemId}")
    @Operation(summary = "Ajouter un article au panier")
    public Panier ajouterItemAuPanier(@PathVariable Long panierId, @PathVariable Long itemId) {
        return panierService.ajouterItemAuPanier(panierId, itemId);
    }

    @PutMapping("/{panierId}/supprimer-item/{itemId}")
    @Operation(summary = "Supprimer un article du panier")
    public Panier supprimerItemDuPanier(@PathVariable Long panierId, @PathVariable Long itemId) {
        return panierService.supprimerItemDuPanier(panierId, itemId);
    }

    @GetMapping("/{panierId}/total")
    @Operation(summary = "Calculer le total du panier")
    public BigDecimal calculerTotalPanier(@PathVariable Long panierId) {
        return panierService.calculerTotalPanier(panierId);
    }
}

