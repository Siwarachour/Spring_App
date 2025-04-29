package tn.esprit.back.Controllers.Marketplace;

import com.nimbusds.jose.util.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.back.Entities.Marketplace.*;
import tn.esprit.back.Services.Marketplace.ItemService;
import tn.esprit.back.Services.User.CustomUserDetailsService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CustomUserDetailsService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Item> addItem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantityAvailable") int quantityAvailable,
            @RequestParam("category") String categoryStr, // Receive as String
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) throws IOException {

        // Convert String to enum safely
        ItemCategory category;
        try {
            category = ItemCategory.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category value");
        }

        int userId = userService.getConnectedUser().getId();

        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        item.setQuantityAvailable(quantityAvailable); // Et ici

        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            item.setImageData(Base64.getEncoder().encodeToString(image.getBytes()));
            item.setImageType(image.getContentType());
        }

        return ResponseEntity.ok(itemService.addItem(item, userId));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getItemImage(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        if (item == null || item.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Base64.getDecoder().decode(item.getImageData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(item.getImageType()))
                .body(imageBytes);
    }
    // ... rest of the controller methods remain the same



    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantityAvailable") int quantityAvailable, // Champ manquant ajouté ici

            @RequestParam("category") ItemCategory category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "keepExistingImage", defaultValue = "false") boolean keepExistingImage,
            Authentication authentication) throws IOException {

        int userId = userService.getConnectedUser().getId();

        Item item = new Item();
        item.setIdItem(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        item.setQuantityAvailable(quantityAvailable); // Et ici

        item.setCategory(category);

        // Gestion de l'image
        if (image != null && !image.isEmpty()) {
            // Convertir l'image en Base64
            item.setImageData(Base64.getEncoder().encodeToString(image.getBytes()));
            item.setImageType(image.getContentType());
        } else if (keepExistingImage) {
            // Garder l'image existante
            Item existingItem = itemService.getItemById(id);
            if (existingItem != null) {
                item.setImageData(existingItem.getImageData());
                item.setImageType(existingItem.getImageType());
            }
        }

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
        // Log pour débogage
        System.out.println("User authenticated: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());

        int userId = userService.getConnectedUser().getId();
        System.out.println("Fetching items for user ID: " + userId);

        List<Item> items = itemService.getItemsBySeller(userId);
        System.out.println("Found " + items.size() + " items");

        return ResponseEntity.ok(items);
    }
    // Ajoutez ces endpoints dans votre ItemController
    @GetMapping("/my-pending")
    public ResponseEntity<List<Item>> getMyPendingItems(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(itemService.getItemsBySellerAndStatus(userId, ItemStatus.PENDING));
    }

    @GetMapping("/my-rejected")
    public ResponseEntity<List<Item>> getMyRejectedItems(Authentication authentication) {
        int userId = userService.getConnectedUser().getId();
        return ResponseEntity.ok(itemService.getItemsBySellerAndStatus(userId, ItemStatus.REJECTED));
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
        if (authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
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
    // Dans votre ItemController

}