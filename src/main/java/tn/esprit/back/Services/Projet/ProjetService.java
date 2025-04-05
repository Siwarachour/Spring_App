package tn.esprit.back.Services.Projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Projet.Projet;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.enums.Status;
import tn.esprit.back.Repository.Projet.ProjetRepository;
import tn.esprit.back.Repository.Projet.TacheRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetService {
    @Autowired
    private ProjetRepository projetRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private TacheRepository tacheRepo;

    public Projet createProjet(Projet projet, String username) {
        User user = userRepo.findByusername(username);
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        projet.setCreateur(user);
        return projetRepo.save(projet);
    }


    public Tache addTache(int projetId, Tache tache) {
        Projet projet = projetRepo.findById(projetId).orElseThrow();

        // Vérifie les limites du nombre de tâches
        if (projet.getTaches().size() >= projet.getNbreGestions()) {
            throw new RuntimeException("Nombre max de tâches atteint");
        }

        // Vérifie les dates par rapport au projet
        if (tache.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("La date de fin de la tâche dépasse celle du projet");
        }

        if (tache.getDateDebut().isBefore(projet.getDateDebut())) {
            throw new RuntimeException("La date de début de la tâche est antérieure à celle du projet");
        }

        // Liaison, statut, et ajout
        tache.setProjet(projet);
        updateTacheStatus(tache);
        projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() + 1);
        projet.getTaches().add(tache);
        updateProjetStatus(projet);

        return tacheRepo.save(tache);
    }


    private void updateTacheStatus(Tache tache) {
        if (tache.getUtilisateur() == null) {
            tache.setStatus(Status.NOT_BEGIN);
        } else if (LocalDate.now().isBefore(tache.getDateFin())) {
            tache.setStatus(Status.EN_COURS);
        } else {
            tache.setStatus(Status.FINISHED);
        }
        tacheRepo.save(tache);
    }

    private void updateProjetStatus(Projet projet) {
        boolean hasTaches = !projet.getTaches().isEmpty();
        boolean allTachesFinished = projet.getTaches().stream()
                .allMatch(t -> t.getStatus() == Status.FINISHED);
        boolean anyTacheEnCours = projet.getTaches().stream()
                .anyMatch(t -> t.getStatus() == Status.EN_COURS);

        if (LocalDate.now().isAfter(projet.getDateFin()) || allTachesFinished) {
            projet.setStatus(Status.FINISHED);
        } else if (hasTaches && anyTacheEnCours) {
            projet.setStatus(Status.EN_COURS);
        } else if (LocalDate.now().isBefore(projet.getDateDebut())) {
            projet.setStatus(Status.NOT_BEGIN);
        }

        projetRepo.save(projet);
    }


    public String participate(int projetId, String username) {
        Projet projet = projetRepo.findById(projetId).orElseThrow();
        if (projet.getNbreMembreDisponible() > 0) {
            User user = userRepo.findByusername(username);
            if (user == null) {
                throw new RuntimeException("Utilisateur non trouvé");
            }

            List<Tache> tachesNonAssignees = projet.getTaches().stream()
                    .filter(t -> t.getUtilisateur() == null).collect(Collectors.toList());

            if (!tachesNonAssignees.isEmpty()) {
                Tache tache = tachesNonAssignees.get(0);
                tache.setUtilisateur(user);
                updateTacheStatus(tache);

                projet.getMembres().add(user);
                projet.setNbreMembreDisponible(projet.getNbreMembreDisponible() - 1);
                updateProjetStatus(projet);
                return "Participation réussie";
            }
        }
        return "Aucune tâche disponible";
    }
    public Projet updateProjet(int id, Projet updated) {
        Projet projet = projetRepo.findById(id).orElseThrow();
        projet.setNomProjet(updated.getNomProjet());
        projet.setDescription(updated.getDescription());
        projet.setDateFin(updated.getDateFin());
        projet.setNbreGestions(updated.getNbreGestions());
        return projetRepo.save(projet);
    }

    public void deleteProjet(int id) {
        projetRepo.deleteById(id);
    }

    public List<Projet> getAllProjets() {
        return projetRepo.findAll();
    }

    public List<Tache> getTachesDuProjet(int projetId) {
        Projet projet = projetRepo.findById(projetId).orElseThrow();
        return projet.getTaches();
    }

    public Tache updateTache(int id, Tache updated) {
        Tache tache = tacheRepo.findById(id).orElseThrow();
        Projet projet = tache.getProjet();

        // vérifier que dateFin de la tâche ≤ projet.dateFin
        if (updated.getDateFin().isAfter(projet.getDateFin())) {
            throw new RuntimeException("La date de fin de la tâche ne peut pas dépasser celle du projet");
        }

        tache.setNomTache(updated.getNomTache());
        tache.setDateDebut(updated.getDateDebut());
        tache.setDateFin(updated.getDateFin());
        return tacheRepo.save(tache);
    }

    public void deleteTache(int id) {
        tacheRepo.deleteById(id);
    }

}
