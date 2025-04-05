package tn.esprit.back.Controllers.Projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Services.Projet.ProjetService;

@RestController
@RequestMapping("/api/taches")
public class TacheController {

    @Autowired
    private ProjetService projetService;

    // UPDATE TÂCHE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTache(@PathVariable int id, @RequestBody Tache tache) {
        return ResponseEntity.ok(projetService.updateTache(id, tache));
    }

    // DELETE TÂCHE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTache(@PathVariable int id) {
        projetService.deleteTache(id);
        return ResponseEntity.ok("Tâche supprimée");
    }
}
