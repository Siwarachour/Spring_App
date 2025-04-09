package tn.esprit.back.Controllers.Marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Services.Marketplace.IItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @PostMapping
    public ResponseEntity<Item> ajouterItem(@RequestBody Item item) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Item createdItem = itemService.ajouterItem(item, authentication);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        item.setId(id);
        Item updatedItem = itemService.updateItem(item);
        return ResponseEntity.ok(updatedItem);
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
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.supprimerItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Item>> getItemsBySeller(@PathVariable int sellerId) {
        List<Item> items = itemService.getItemsBySeller(sellerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Item>> getItemsPendingApproval() {
        List<Item> items = itemService.getItemsPendingApproval();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveItem(@PathVariable Long id) {
        itemService.approveItem(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectItem(@PathVariable Long id) {
        itemService.rejectItem(id);
        return ResponseEntity.ok().build();
    }
}