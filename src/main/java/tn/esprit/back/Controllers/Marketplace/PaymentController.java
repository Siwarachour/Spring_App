package tn.esprit.back.Controllers.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Services.Marketplace.PaymentService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/marketplace/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomUserDetailsService userService;

    @PostMapping("/checkout")
    public ResponseEntity<Facture> checkout(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(paymentService.createFactureFromPanier(userId));
    }

    @PostMapping("/pay/{factureId}")
    public ResponseEntity<Paiement> processPayment(
            @PathVariable Long factureId,
            @RequestParam PaymentMethod methode,
            @RequestParam String reference,
            Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(paymentService.processPayment(factureId, methode, reference));
    }

    @GetMapping("/my-factures")
    public ResponseEntity<List<Facture>> getMyFactures(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(paymentService.getUserFactures(userId));
    }
    @GetMapping("/facture/{factureId}/payment")
    public ResponseEntity<Paiement> getPaymentByFacture(
            @PathVariable Long factureId,
            Authentication authentication) {

        int userId = userService.getConnectedUser().getId();

        Optional<Paiement> optionalPaiement = paymentService.getPaymentByFacture(factureId);

        if (optionalPaiement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Paiement paiement = optionalPaiement.get();

        if (paiement.getFacture().getClient().getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(paiement);
    }
}