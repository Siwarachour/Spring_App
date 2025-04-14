package tn.esprit.back.Controllers.Projet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Services.Projet.ProjetService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;

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
        projetService.updateProjectStatus(projetId);
        return ResponseEntity.ok(projetService.addTacheToProjet(projetId, userId, tache));
    }

    @DeleteMapping("/{projetId}/tache/{tacheId}/delete")
    public ResponseEntity<Projet> deleteTacheFromProjet(@PathVariable int projetId,
                                                        @PathVariable int tacheId) {
        return ResponseEntity.ok(projetService.deleteTacheFromProjet(projetId, tacheId));
    }

    @PutMapping("/{projetId}/tache/{tacheId}/update")
    public ResponseEntity<Projet> updateTacheFromProjet(@PathVariable int projetId,
                                                        @PathVariable int tacheId,
                                                        @RequestBody Tache updatedTache) {
        projetService.updateProjectStatus(projetId);
        return ResponseEntity.ok(projetService.updateTacheFromProjet(projetId, tacheId, updatedTache));
    }

    @PostMapping("/{projetId}/participate/{userId}")
    public ResponseEntity<Projet> participateToProjet(@PathVariable int projetId, @PathVariable int userId) {
        return ResponseEntity.ok(projetService.participateToProjet(projetId, userId));
    }

    @PostMapping("/{projetId}/upload-image")
    public ResponseEntity<Projet> uploadImageToProjet(@PathVariable int projetId,
                                                      @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(projetService.uploadImageToProjet(projetId, file));
    }



    @GetMapping("/{id}")
    public ResponseEntity<Projet> getProjetById(@PathVariable int id) {
        projetService.updateProjectStatus(id);
        return ResponseEntity.ok(projetService.getProjetById(id));

    }
}
