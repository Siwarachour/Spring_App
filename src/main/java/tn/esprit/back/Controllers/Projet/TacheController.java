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




}
