package tn.esprit.back.Controllers.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Services.Marketplace.ItemService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CustomUserDetailsService userService;

    @PostMapping
    public ResponseEntity<Item> addItem(@RequestBody Item item, Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(itemService.addItem(item, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item, Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        item.setIdItem(id);
        return ResponseEntity.ok(itemService.updateItem(item, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        itemService.deleteItem(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<Item>> getMyItems(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(itemService.getItemsBySeller(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Item>> getPendingItems(Authentication authentication) {
        // Vérifier si l'utilisateur est admin
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(itemService.getPendingItems());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Item> approveItem(@PathVariable Long id, Authentication authentication) {
        // Vérifier si l'utilisateur est admin
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(itemService.approveItem(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Item> rejectItem(@PathVariable Long id, Authentication authentication) {
        // Vérifier si l'utilisateur est admin
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(itemService.rejectItem(id));
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAvailableItems() {
        return ResponseEntity.ok(itemService.getAvailableItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable ItemCategory category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }
}