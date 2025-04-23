package tn.esprit.back.Services.Projet;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.enums.Status;
import tn.esprit.back.Repository.Projet.ProjetRepository;
import tn.esprit.back.Repository.Projet.TacheRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final TacheRepository tacheRepository;
    private final UserRepository userRepository;

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
        Projet projet = getProjetById(id);
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

    public Projet addTacheToProjet(int projetId, int userId, Tache tache) {
        Projet projet = getProjetById(projetId);
        User user = getUserById(userId);

        if (tache.getDateDebut().isBefore(projet.getDateDebut()) || tache.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("La date de la tâche doit être entre " + projet.getDateDebut() + " et " + projet.getDateFin());
        }

        if (projet.getTaches() != null && projet.getTaches().size() >= projet.getNbreGestions()) {
            throw new RuntimeException("Nombre maximal de tâches atteint.");
        }

        tache.setProjet(projet);
        tache.setUtilisateur(user);
        tache.setStatus(Status.NOT_BEGIN);
        Tache savedTache = tacheRepository.save(tache);

        if (projet.getTaches() == null) {
            projet.setTaches(new ArrayList<>());
        }
        projet.getTaches().add(savedTache);

        if (projet.getNbreMembreDisponible() < projet.getNbreGestions()) {
            projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() + 1);
        }

        return projetRepository.save(projet);
    }

    public Projet deleteTacheFromProjet(int projetId, int tacheId) {
        Projet projet = getProjetById(projetId);
        Tache tache = getTacheById(tacheId);

        projet.getTaches().remove(tache);
        tacheRepository.delete(tache);

        if (projet.getNbreMembreDisponible() > 0) {
            projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() - 1);
        }

        return projetRepository.save(projet);
    }


    public Projet updateTacheFromProjet(int projetId, int tacheId, Tache updatedTache) {
        Projet projet = getProjetById(projetId);
        Tache tache = getTacheById(tacheId);

        if (updatedTache.getDateDebut().isBefore(projet.getDateDebut()) || updatedTache.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("La date de la tâche est hors du projet.");
        }

        tache.setNomTache(updatedTache.getNomTache());
        tache.setDateDebut(updatedTache.getDateDebut());
        tache.setDateFin(updatedTache.getDateFin());
        tache.setStatus(updatedTache.getStatus());
        tache.setUtilisateur(updatedTache.getUtilisateur());

        tacheRepository.save(tache);
        updateProjectStatus(projetId);

        return projetRepository.save(projet);
    }

    public void ajouterParticipant(int idProjet, int idUser) {
        Projet projet = getProjetById(idProjet);
        User user = getUserById(idUser);

        if (projet.getMembres().contains(user)) {
            throw new RuntimeException("Utilisateur déjà inscrit.");
        }

        if (projet.getNbreMembreDisponible() <= 0) {
            throw new RuntimeException("Aucune place disponible.");
        }

        projet.getMembres().add(user);
        projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() - 1);
        projetRepository.save(projet);
    }

    public Projet uploadImageToProjet(int projetId, MultipartFile file) throws IOException {
        Projet projet = getProjetById(projetId);

        if (file != null && !file.isEmpty()) {
            // Extraire l'extension du fichier
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";

            // Créer un nom de fichier unique en utilisant UUID
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Définir le chemin d'enregistrement du fichier
            Path path = Paths.get(uploadDir, uniqueFileName);

            // Enregistrer le fichier dans le répertoire spécifié
            Files.copy(file.getInputStream(), path);

            // Mettre à jour l'image du projet avec le nouveau nom de fichier
            projet.setImage(uniqueFileName);
        }

        // Sauvegarder et retourner le projet avec la nouvelle image
        return projetRepository.save(projet);
    }

    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    public Projet getProjetById(int id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet avec ID " + id + " non trouvé."));
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec ID " + id + " non trouvé."));
    }

    public Tache getTacheById(int id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche avec ID " + id + " non trouvée."));
    }

    public List<Tache> getTachesByUserId(int userId) {
        return projetRepository.findAll().stream()
                .flatMap(projet -> projet.getTaches().stream())
                .filter(tache -> tache.getUtilisateur() != null && tache.getUtilisateur().getId() == userId)
                .collect(Collectors.toList());
    }

    public void updateProjectStatus(int projetId) {
        Projet projet = getProjetById(projetId);

        boolean hasEnCours = false;
        boolean allFinished = true;

        for (Tache tache : projet.getTaches()) {
            if (tache.getStatus() != Status.FINISHED && LocalDate.now().isAfter(tache.getDateFin())) {
                tache.setStatus(Status.FINISHED);
                tacheRepository.save(tache);
            }

            if (tache.getStatus() == Status.EN_COURS) {
                hasEnCours = true;
            }

            if (tache.getStatus() != Status.FINISHED) {
                allFinished = false;
            }
        }

        if (projet.getTaches().isEmpty()) {
            projet.setStatus(Status.NOT_BEGIN);
        } else if (allFinished) {
            projet.setStatus(Status.FINISHED);
        } else if (hasEnCours) {
            projet.setStatus(Status.EN_COURS);
        } else {
            projet.setStatus(Status.NOT_BEGIN);
        }

        projetRepository.save(projet);
    }


    public Tache updateTache(int tacheId, Tache updatedTache) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche avec ID " + tacheId + " non trouvée."));

        // Mettre à jour les informations de la tâche
        tache.setNomTache(updatedTache.getNomTache());
        tache.setDateDebut(updatedTache.getDateDebut());
        tache.setDateFin(updatedTache.getDateFin());
        tache.setDescription(updatedTache.getDescription());
        tache.setStatus(updatedTache.getStatus());
        tache.setImage(updatedTache.getImage());
        tache.setLanguage(updatedTache.getLanguage());


        // Sauvegarder la tâche mise à jour dans la base de données
        return tacheRepository.save(tache);
    }

    public List<Tache> getTachesByProjet(int idProjet) {
        Projet projet = getProjetById(idProjet); // récupère le projet avec ses tâches
        return projet.getTaches(); // retourne directement les tâches du projet
    }

    public List<Tache> getAllTaches() {
        return tacheRepository.findAll(); // récupère toutes les tâches
    }

    public Tache findTacheById(int tacheId) {
        return tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche avec ID " + tacheId + " non trouvée."));
    }

    public Tache saveTache(Tache tache) {
        return tacheRepository.save(tache);
    }


    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        List<Projet> projets = projetRepository.findAll();
        List<Tache> taches = tacheRepository.findAll();

        // Projets
        stats.put("totalProjets", projets.size());
        stats.put("projetsNotBegin", projets.stream().filter(p -> p.getStatus() == Status.NOT_BEGIN).count());
        stats.put("projetsEnCours", projets.stream().filter(p -> p.getStatus() == Status.EN_COURS).count());
        stats.put("projetsFinished", projets.stream().filter(p -> p.getStatus() == Status.FINISHED).count());

        // Moyenne de tâches par projet
        double moyenneTachesParProjet = projets.stream()
                .mapToInt(p -> p.getTaches() != null ? p.getTaches().size() : 0)
                .average().orElse(0.0);
        stats.put("moyenneTachesParProjet", moyenneTachesParProjet);

        // Tâches
        stats.put("totalTaches", taches.size());
        stats.put("tachesNotBegin", taches.stream().filter(t -> t.getStatus() == Status.NOT_BEGIN).count());
        stats.put("tachesEnCours", taches.stream().filter(t -> t.getStatus() == Status.EN_COURS).count());
        stats.put("tachesFinished", taches.stream().filter(t -> t.getStatus() == Status.FINISHED).count());

        return stats;
    }



}