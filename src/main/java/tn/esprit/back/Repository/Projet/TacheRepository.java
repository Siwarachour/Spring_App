package tn.esprit.back.Repository.Projet;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.back.Entities.Projet.Tache;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Integer> {


  }
