package tn.esprit.back.Controllers.Marketplace;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Services.Marketplace.PanierService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/paniers")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @PostMapping
    @Operation(summary = "Créer un panier pour l'utilisateur courant")
    public Panier createPanierForCurrentUser() {
        return panierService.ajouterPanier(new Panier());
    }

    @GetMapping("/mon-panier")
    @Operation(summary = "Récupérer le panier de l'utilisateur courant")
    public Panier getCurrentUserPanier() {
        return panierService.getOrCreatePanierForCurrentUser();
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les paniers (Admin seulement)")
    public Iterable<Panier> getAllPaniers() {
        return panierService.getAllPaniers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un panier par son ID (Admin seulement)")
    public Panier getPanierById(@PathVariable Long id) {
        return panierService.getPanierById(id)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un panier (Admin seulement)")
    public void supprimerPanier(@PathVariable Long id) {
        panierService.supprimerPanier(id);
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<Panier> ajouterItemAuPanier(@PathVariable Long itemId) {
        Panier panier = panierService.ajouterItemAuPanier(itemId);
        return ResponseEntity.ok(panier);
    }
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Supprimer un article du panier de l'utilisateur courant")
    public Panier supprimerItemDuPanier(@PathVariable Long itemId) {
        return panierService.supprimerItemDuPanier(itemId);
    }

    @GetMapping("/total")
    @Operation(summary = "Calculer le total du panier de l'utilisateur courant")
    public BigDecimal calculerTotalPanier() {
        Panier panier = panierService.getOrCreatePanierForCurrentUser();
        return panierService.calculerTotalPanier(panier.getId());
    }
}