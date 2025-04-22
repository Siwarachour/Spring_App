package tn.esprit.back.Controllers.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Services.Marketplace.PanierService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.util.Map;

@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
public class PanierController {
    private final PanierService panierService;
    private final CustomUserDetailsService userService;

    @GetMapping
    public ResponseEntity<Panier> getPanier(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(panierService.getPanier(userId));
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<?> addItemToPanier(@PathVariable Long itemId, Authentication authentication) {
        try {
            int userId = userService.getConnectedUser().getId();
            Panier panier = panierService.addItemToPanier(userId, itemId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Item added to cart",
                    "cart", panier
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Panier> removeItemFromPanier(@PathVariable Long itemId, Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(panierService.removeItemFromPanier(userId, itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearPanier(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        panierService.clearPanier(userId);
        return ResponseEntity.noContent().build();
    }
}