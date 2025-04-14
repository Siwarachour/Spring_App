package tn.esprit.back.Services.Projet;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.enums.Status;
import tn.esprit.back.Repository.Projet.ProjetRepository;
import tn.esprit.back.Repository.Projet.TacheRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final TacheRepository tacheRepository;
    private final UserRepository userRepository;

    // Définir le répertoire d'upload des images
    @Value("${file.upload-dir}")
    private String uploadDir;


    public Projet addProjet(Projet projet) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByusername(username);

        projet.setCreateur(user);
        projet.setStatus(Status.NOT_BEGIN);

        return projetRepository.save(projet);

    }

    public Projet updateProjet(int id, Projet updatedProjet) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
        projet.setNomProjet(updatedProjet.getNomProjet());
        projet.setDescription(updatedProjet.getDescription());
        projet.setDateDebut(updatedProjet.getDateDebut());
        projet.setDateFin(updatedProjet.getDateFin());
        projet.setNbreGestions(updatedProjet.getNbreGestions());

        return projetRepository.save(projet);
    }

    public void deleteProjet(int id) {
        projetRepository.deleteById(id);
    }


    public Projet addTacheToProjet(int projetId, int userId, Tache tache ) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier que la tâche est dans la période du projet
        if (tache.getDateDebut().isBefore(projet.getDateDebut()) || tache.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("Tâche date out of project bounds");
        }

        // Vérifier que le nombre de tâches ne dépasse pas le nombre de gestions
        if (projet.getTaches() != null && projet.getTaches().size() >= projet.getNbreGestions()) {
            throw new RuntimeException("Nombre maximal de tâches atteint");
        }

       // La langue passée en paramètre

        // Ajouter la tâche au projet
        tache.setProjet(projet);
        tache.setUtilisateur(user);
        tache.setStatus(Status.NOT_BEGIN);
        Tache savedTache = tacheRepository.save(tache);


        if (projet.getTaches() == null) {
            projet.setTaches(new ArrayList<>());
        }

        projet.getTaches().add(savedTache);

        // Incrémenter le nombre de membres disponibles (sans dépasser le nombre de gestions)
        if (projet.getNbreMembreDisponible() < projet.getNbreGestions()) {
            projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() + 1);
        }

        return projetRepository.save(projet);
    }


    public Projet deleteTacheFromProjet(int projetId, int tacheId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));

        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche not found"));

        // Supprimer la tâche de la liste
        projet.getTaches().remove(tache);

        // Supprimer la tâche de la base de données
        tacheRepository.delete(tache);

        // Diminuer le nombre de membres disponibles
        if (projet.getNbreMembreDisponible() > 0) {
            projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() - 1);
        }

        // Mettre à jour le statut du projet
        updateProjectStatus(projetId);

        return projetRepository.save(projet);
    }


    public Projet updateTacheFromProjet(int projetId, int tacheId, Tache updatedTache) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche not found"));

        if (updatedTache.getDateDebut().isBefore(projet.getDateDebut()) || updatedTache.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("Tâche date out of project bounds");
        }

        tache.setNomTache(updatedTache.getNomTache());
        tache.setDateDebut(updatedTache.getDateDebut());
        tache.setDateFin(updatedTache.getDateFin());
        tache.setStatus(updatedTache.getStatus());
        tache.setUtilisateur(updatedTache.getUtilisateur());

        tacheRepository.save(tache);
        return projetRepository.save(projet);
    }
    public void updateProjectStatus(int projectId) {
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));

        List<Tache> tasks = projet.getTaches(); // Tu dois avoir le getter `getTaches()` dans Projet

        if (tasks.isEmpty()) {
            projet.setStatus(Status.NOT_BEGIN);
        } else {
            boolean anyInProgress = tasks.stream().anyMatch(t -> t.getStatus() == Status.EN_COURS);
            boolean allFinished = tasks.stream().allMatch(t -> t.getStatus() == Status.FINISHED);

            if (anyInProgress) {
                projet.setStatus(Status.EN_COURS);
            } else if (allFinished) {
                projet.setStatus(Status.FINISHED);
            } else {
                projet.setStatus(Status.NOT_BEGIN);
            }
        }

        projetRepository.save(projet);
    }


    public Projet participateToProjet(int projetId, int userId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!projet.getMembres().contains(user)) {
            projet.getMembres().add(user);
        }

        return projetRepository.save(projet);
    }

    public Projet uploadImageToProjet(int projetId, MultipartFile file) throws IOException {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found"));

        // Vérifier si l'image est présente et sauvegarder le fichier
        if (file != null && !file.isEmpty()) {
            // Récupérer le nom de l'image
            String fileName = file.getOriginalFilename();

            // Définir le chemin complet où le fichier sera sauvegardé
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), path);

            // Mettre à jour l'image du projet avec le nom du fichier
            projet.setImage(fileName);  // Enregistrer le nom du fichier dans la base de données
        }

        return projetRepository.save(projet);
    }

    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    public Projet getProjetById(int id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
    }
}
