package tn.esprit.back.Controllers.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Paiement;
import tn.esprit.back.Services.Marketplace.PaiementService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    // Ajouter un paiement
    @PostMapping
    public ResponseEntity<Paiement> ajouterPaiement(@RequestBody Paiement paiement) {
        Paiement newPaiement = paiementService.ajouterPaiement(paiement);
        return ResponseEntity.ok(newPaiement);
    }

    // Mettre à jour un paiement
    @PutMapping("/{id}")
    public ResponseEntity<Paiement> updatePaiement(@PathVariable Long id, @RequestBody Paiement paiement) {
        paiement.setId(id);
        Paiement updatedPaiement = paiementService.updatePaiement(paiement);
        return ResponseEntity.ok(updatedPaiement);
    }

    // Récupérer tous les paiements
    @GetMapping
    public ResponseEntity<List<Paiement>> getAllPaiements() {
        List<Paiement> paiements = paiementService.getAllPaiements();
        return ResponseEntity.ok(paiements);
    }

    // Récupérer un paiement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable Long id) {
        Optional<Paiement> paiement = paiementService.getPaiementById(id);
        return paiement.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer un paiement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPaiement(@PathVariable Long id) {
        paiementService.supprimerPaiement(id);
        return ResponseEntity.noContent().build();
    }

    // Effectuer un paiement
    @PostMapping("/{transactionId}/effectuer")
    public ResponseEntity<Paiement> effectuerPaiement(@PathVariable Long transactionId, @RequestParam String modePaiement) {
        Paiement paiement = paiementService.effectuerPaiement(transactionId, modePaiement);
        return ResponseEntity.ok(paiement);
    }
}
