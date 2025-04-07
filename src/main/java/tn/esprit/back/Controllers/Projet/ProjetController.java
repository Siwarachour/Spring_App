package tn.esprit.back.Controllers.Projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Services.Projet.ProjetService;

import java.security.Principal;

@RestController
@RequestMapping("/api/projets")
public class ProjetController {
    @Autowired
    private ProjetService projetService;

    @PostMapping
    public ResponseEntity<?> createProjet(@RequestBody Projet projet, Principal principal) {
        System.out.println("Utilisateur connecté : " + principal.getName());
        return ResponseEntity.ok(projetService.createProjet(projet, principal.getName()));
    }

    @PostMapping("/{id}/taches")
    public ResponseEntity<?> addTache(@PathVariable int id, @RequestBody Tache tache) {
        return ResponseEntity.ok(projetService.addTache(id, tache));
    }

    @PostMapping("/{id}/participate")
    public ResponseEntity<?> participate(@PathVariable int id, Principal principal) {
        return ResponseEntity.ok(projetService.participate(id, principal.getName()));
    }
    // UPDATE PROJET
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjet(@PathVariable int id, @RequestBody Projet updatedProjet) {
        return ResponseEntity.ok(projetService.updateProjet(id, updatedProjet));
    }

    // DELETE PROJET
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjet(@PathVariable int id) {
        projetService.deleteProjet(id);
        return ResponseEntity.ok("Projet supprimé");
    }

    // LISTE DES PROJETS
    @GetMapping
    public ResponseEntity<?> getAllProjets() {
        return ResponseEntity.ok(projetService.getAllProjets());
    }

    // LISTE DES TÂCHES D'UN PROJET
    @GetMapping("/{id}/taches")
    public ResponseEntity<?> getTachesDuProjet(@PathVariable int id) {
        return ResponseEntity.ok(projetService.getTachesDuProjet(id));
    }

}
