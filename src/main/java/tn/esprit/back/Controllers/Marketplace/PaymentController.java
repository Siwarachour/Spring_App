package tn.esprit.back.Controllers.Marketplace;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Facture;
import tn.esprit.back.Entities.Marketplace.Paiement;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Services.Marketplace.PanierService;
import tn.esprit.back.Services.Marketplace.PaymentService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomUserDetailsService userService;

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<Map<String, String>> handleStripeException(StripeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @PostMapping("/initier-paiement")
    public ResponseEntity<?> initierPaiement(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication required"));
            }

            User user = userService.getConnectedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            Map<String, String> paymentInfo = paymentService.initierPaiement(user.getId());
            return ResponseEntity.ok(paymentInfo);

        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Stripe error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/confirmer-paiement/{paymentIntentId}")
    public ResponseEntity<?> confirmerPaiement(
            @PathVariable String paymentIntentId,
            Authentication authentication) {
        try {
            // Verify authentication
            User user = userService.getConnectedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            Facture facture = paymentService.confirmerPaiementReussi(paymentIntentId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "facture", facture,
                    "receiptUrl", facture.getPaymentReceiptUrl()
            ));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Stripe error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    @GetMapping("/mes-paiements")
    public ResponseEntity<?> getPaiementsUtilisateur(Authentication authentication) {
        try {
            User user = userService.getConnectedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            List<Paiement> paiements = paymentService.getPaiementsByUserId(user.getId());
            return ResponseEntity.ok(paiements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/mes-factures")
    public ResponseEntity<?> getFacturesUtilisateur(Authentication authentication) {
        try {
            User user = userService.getConnectedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            List<Facture> factures = paymentService.getFacturesByUserId(user.getId());
            return ResponseEntity.ok(factures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

//    @PostMapping("/webhook")
//    public ResponseEntity<Void> handleWebhook(
//            @RequestBody String payload,
//            @RequestHeader("Stripe-Signature") String sigHeader) {
//        try {
//            // Validation de la signature Stripe (à implémenter)
//            String paymentIntentId = extractPaymentIntentId(payload);
//            paymentService.handlePaymentWebhook(paymentIntentId);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    private String extractPaymentIntentId(String payload) {
        // Implémentation basique - à améliorer
        JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
        JsonObject dataObject = jsonObject.getAsJsonObject("data");
        JsonObject paymentIntentObject = dataObject.getAsJsonObject("object");
        return paymentIntentObject.get("id").getAsString();
    }
}