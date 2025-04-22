package tn.esprit.back.Controllers.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Panier;
import tn.esprit.back.Services.Marketplace.PanierService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

@RestController
@RequestMapping("/api/marketplace/panier")
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
    public ResponseEntity<Panier> addItemToPanier(@PathVariable Long itemId, Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(panierService.addItemToPanier(userId, itemId));
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