package tn.esprit.back.Controllers.coursCertificat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.coursCertificat.Cours;
import tn.esprit.back.Services.coursCertificat.ICoursService;

import java.util.List;

@RestController
@RequestMapping("/cours")
public class CoursController {
    @Autowired
    ICoursService coursService;

    @PostMapping("/addCours")
    Cours addCours(@RequestBody Cours cours) {
        return coursService.addCours(cours);
    }
    @PutMapping("/updateCours")
    Cours updateCours(@RequestBody Cours cours) {
        return coursService.updateCours(cours);
    }
    @GetMapping("/getAllCours")
    List<Cours> getAllCours() {
        return coursService.getAllCours();
    }
    @GetMapping("/getCoursById/{idcours}")
    Cours getCoursById(@PathVariable long idcours) {
        return coursService.getCoursById(idcours);
    }
    @DeleteMapping("/deleteCours/{idcours}")
    Cours deleteCours(@PathVariable long idcours) {
        coursService.deleteCours(idcours);
        return null;
    }
}
