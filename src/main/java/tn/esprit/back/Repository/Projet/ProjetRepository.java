package tn.esprit.back.Repository.Projet;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Projet.Projet;

public interface ProjetRepository extends JpaRepository<Projet, Integer> {
}
