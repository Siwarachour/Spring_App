package tn.esprit.back.Controllers.Projet;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Repository.Projet.TacheRepository;
import tn.esprit.back.Services.Projet.ProjetService;
import tn.esprit.back.Services.Projet.TacheService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;
    private final TacheRepository tacheRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Projet>> getAllProjets() {
        return ResponseEntity.ok(projetService.getAllProjets());
    }


    @PostMapping("/add")
    public ResponseEntity<Projet> addProjet(@RequestParam("nomProjet") String nomProjet,
                                            @RequestParam("description") String description,
                                            @RequestParam("dateDebut") String dateDebutStr,
                                            @RequestParam("dateFin") String dateFinStr,
                                            @RequestParam("nbreGestions") int nbreGestions,

                                            @RequestParam(value = "image", required = false) MultipartFile image) {

        // Convertir les chaînes de date en LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");  // Assurez-vous que ce format correspond à celui utilisé dans le formulaire
        LocalDate dateDebut = LocalDate.parse(dateDebutStr, formatter);
        LocalDate dateFin = LocalDate.parse(dateFinStr, formatter);

        Projet projet = new Projet();
        projet.setNomProjet(nomProjet);
        projet.setDescription(description);
        projet.setDateDebut(dateDebut);  // Utilisation de LocalDate
        projet.setDateFin(dateFin);      // Utilisation de LocalDate
projet.setNbreGestions(nbreGestions);

        // Sauvegarde du projet dans la base de données
        Projet savedProjet = projetService.addProjet(projet);

        // Si une image est présente, vous pouvez la traiter ici
        if (image != null && !image.isEmpty()) {
            try {
                projetService.uploadImageToProjet(savedProjet.getIdProjet(), image);  // Utilisez l'ID du projet sauvegardé
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        return ResponseEntity.ok(savedProjet);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Projet> updateProjet(@PathVariable int id, @RequestBody Projet projet) {
        return ResponseEntity.ok(projetService.updateProjet(id, projet));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable int id) {
        projetService.deleteProjet(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projetId}/tache/{userId}/add")
    public ResponseEntity<Projet> addTacheToProjet(@PathVariable int projetId,
                                                   @PathVariable int userId,
                                                   @RequestBody Tache tache) {

        return ResponseEntity.ok(projetService.addTacheToProjet(projetId, userId, tache));
    }

    public ResponseEntity<Projet> deleteTacheFromProjet(@PathVariable int projetId,
                                                        @PathVariable int tacheId) {
        try {
            // Essayer de supprimer la tâche du projet
            Projet projet = projetService.deleteTacheFromProjet(projetId, tacheId);
            return ResponseEntity.ok(projet); // Retourne le projet mis à jour
        } catch (EntityNotFoundException e) {
            // Si le projet ou la tâche n'a pas été trouvé, retourner 404 Not Found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // En cas d'erreur inattendue, retourner 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/{projetId}/tache/{tacheId}/update")
    public ResponseEntity<Projet> updateTacheFromProjet(@PathVariable int projetId,
                                                        @PathVariable int tacheId,
                                                        @RequestBody Tache updatedTache) {

        return ResponseEntity.ok(projetService.updateTacheFromProjet(projetId, tacheId, updatedTache));
    }

    @PostMapping("projets/{projetId}/participate/{userId}")
    public ResponseEntity<String> participerAuProjet(@PathVariable int projetId, @PathVariable int userId) {
        projetService.ajouterParticipant(projetId, userId);
        return ResponseEntity.ok("User has joined the project");
    }


    @PostMapping("/{projetId}/upload-image")
    public ResponseEntity<Projet> uploadImageToProjet(@PathVariable int projetId,
                                                      @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(projetService.uploadImageToProjet(projetId, file));
    }



    @GetMapping("/{id}")
    public ResponseEntity<Projet> getProjetById(@PathVariable int id) {

        return ResponseEntity.ok(projetService.getProjetById(id));

    }

    @GetMapping("/taches/user/{userId}")
    public ResponseEntity<List<Tache>> getTachesByUserId(@PathVariable int userId) {
        List<Tache> taches = projetService.getTachesByUserId(userId);
        return ResponseEntity.ok(taches);
    }


    @PutMapping("/tache/{tacheId}/update")
    public ResponseEntity<Tache> updateTache(@PathVariable int tacheId, @RequestBody Tache updatedTache) {
        try {
            Tache updatedTask = projetService.updateTache(tacheId, updatedTache);
            return ResponseEntity.ok(updatedTask);  // Retourner la tâche mise à jour
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Tâche non trouvée
        }
    }
    @GetMapping("/{id}/taches")
    public List<Tache> getTachesByProjet(@PathVariable("id") int idProjet) {
        return projetService.getTachesByProjet(idProjet);
    }
    @GetMapping("/taches")
    public List<Tache> getAllTaches() {
        return projetService.getAllTaches(); // Récupère et renvoie toutes les tâches
    }


    @DeleteMapping("/{idProjet}/taches/{idTache}")
    public ResponseEntity<String> deleteTask(@PathVariable int idProjet, @PathVariable int idTache) {
        try {
            // Appeler le service pour supprimer la tâche
            projetService.deleteTacheFromProjet(idProjet, idTache);
            return ResponseEntity.ok("Tâche supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de la tâche.");
        }
    }

}
