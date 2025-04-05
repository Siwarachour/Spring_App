package tn.esprit.back.Services.Projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.Projet.Tache;
import tn.esprit.back.Entities.enums.Status;
import tn.esprit.back.Repository.Projet.ProjetRepository;
import tn.esprit.back.Repository.Projet.TacheRepository;
import tn.esprit.back.Repository.User.UserRepository;

import java.time.LocalDate;

@Service
public class TacheService {
    @Autowired
    private TacheRepository tacheRepository;
    @Autowired private UserRepository userRepo;
    @Autowired private TacheRepository tacheRepo;



}
