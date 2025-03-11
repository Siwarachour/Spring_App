package tn.esprit.back.Controllers.Marketplace;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Marketplace.Item;
import tn.esprit.back.Services.Marketplace.IItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @PostMapping
    @Operation(summary = "Ajouter un nouvel article")
    public Item ajouterItem(@RequestBody Item item) {
        return itemService.ajouterItem(item);
    }

    @PutMapping
    @Operation(summary = "Mettre à jour un article")
    public Item updateItem(@RequestBody Item item) {
        return itemService.updateItem(item);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les articles")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un article par son ID")
    public Optional<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un article par son ID")
    public void supprimerItem(@PathVariable Long id) {
        itemService.supprimerItem(id);
    }
}
