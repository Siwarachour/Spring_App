package tn.esprit.back.Controllers.Marketplace;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Entities.Marketplace.Category;
import tn.esprit.back.Services.Marketplace.IItemService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> ajouterItem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("sellerId") Long sellerId,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {  // Changed to @RequestPart

        try {
            // Enhanced validation
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }

            // File validation
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) {
                        return ResponseEntity.badRequest().body("File cannot be empty");
                    }
                    if (!file.getContentType().startsWith("image/")) {
                        return ResponseEntity.badRequest().body("Only image files are allowed");
                    }
                    if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                        return ResponseEntity.badRequest().body("File size exceeds 5MB limit");
                    }
                }
            }

            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(Category.valueOf(category.toUpperCase()));
            // Initialize images list if null
            if (item.getImages() == null) {
                item.setImages(new ArrayList<>());
            }

            Item createdItem = itemService.ajouterItem(item, files, sellerId);
            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating item: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("sellerId") Long sellerId,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {

        try {
            Item item = new Item();
            item.setId(id);
            item.setTitle(title);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(Category.valueOf(category.toUpperCase()));

            Item updatedItem = itemService.updateItem(item, files, sellerId);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            Long currentUserId = getUserIdFromToken(authHeader.replace("Bearer ", ""));
            itemService.supprimerItem(id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getUserIdFromToken(String token) {
        // Implémentation basique - à adapter selon votre système d'authentification
        // Exemple avec JWT (si vous utilisez jjwt) :
        /*
        Claims claims = Jwts.parser()
                .setSigningKey("votreSecretKey")
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
        */

        // Solution temporaire si vous n'utilisez pas JWT :
        // Extraire l'ID de manière simplifiée (à adapter)
        try {
            // Exemple: suppose que le token contient l'ID après un prefixe
            String[] parts = token.split("\\.");
            if (parts.length > 0) {
                String payload = new String(Base64.getDecoder().decode(parts[1]));
                JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
                return jsonObject.get("id").getAsLong();
            }
            throw new RuntimeException("Invalid token format");
        } catch (Exception e) {
            throw new SecurityException("Failed to extract user ID from token", e);
        }
    }
    

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Item>> getItemsBySeller(@PathVariable Long sellerId) {
        List<Item> items = itemService.getItemsBySeller(sellerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Item>> getItemsPendingApproval() {
        List<Item> items = itemService.getItemsPendingApproval();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveItem(@PathVariable Long id) {
        itemService.approveItem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectItem(@PathVariable Long id) {
        itemService.rejectItem(id);
        return ResponseEntity.ok().build();
    }
}