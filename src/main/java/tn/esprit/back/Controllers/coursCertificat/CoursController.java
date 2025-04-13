package tn.esprit.back.Controllers.coursCertificat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.coursCertificat.*;
import tn.esprit.back.Services.coursCertificat.ICoursService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cours")
public class CoursController {
    @Autowired
    ICoursService coursService;

    /*@PostMapping("/addCours")
    Cours addCours(@RequestBody Cours cours) {
        return coursService.addCours(cours);
    }*/
   /* @PutMapping("/updateCours")
    Cours updateCours(@RequestBody Cours cours) {
        return coursService.updateCours(cours);
    } */
    @GetMapping("/getAllCours")
    List<Cours> getAllCours() {
        return coursService.getAllCours();
    }
    @GetMapping("/getCoursById/{idcours}")
    Cours getCoursById(@PathVariable long idcours) {
        return coursService.getCoursById(idcours);
    }

    @GetMapping("/getCoursByTestId/{testId}")
    public ResponseEntity<Cours> getCoursByTestId(@PathVariable long testId) {
        Cours cours = coursService.getCoursByTestId(testId);
        if (cours != null) {
            return ResponseEntity.ok(cours);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/deleteCours/{idcours}")
    Cours deleteCours(@PathVariable long idcours) {
        coursService.deleteCours(idcours);
        return null;
    }
    @PutMapping("/affectCertificatToCours/{idCours}/{idCertificat}")
    Cours affectCertificatToCours(@PathVariable long idCours, @PathVariable long idCertificat) {
        return coursService.affectCertificatToCours(idCours, idCertificat);
    }
    @PostMapping("/addCoursAndAffectCertificat/{idCertificat}")
    Cours addCoursAndAffectCertificat(@RequestBody Cours cours, @PathVariable long idCertificat) {
        return coursService.addCoursAndAffectCertificat(cours, idCertificat);
    }

    @PutMapping("/affectTestToCours/{idCours}/{idTest}")
    Cours affectTestToCours(@PathVariable long idCours, @PathVariable long idTest) {
        return coursService.affectTestToCours(idCours, idTest);
    }
    @PostMapping("/addCoursAndAffectTest/{idTest}")
    Cours addCoursAndAffectTest(@RequestBody Cours cours, @PathVariable long idTest) {
        return coursService.addCoursAndAffectTest(cours, idTest);
    }

    @PostMapping("/addCours")
    public ResponseEntity<Cours> addCours(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("auteur") String auteur,
            @RequestParam("categorie") String categorie,
            @RequestParam("langue") String langue,
            @RequestParam("duree") int duree,
            @RequestParam("prix") Double prix,
            @RequestParam("niveau") NiveauCours niveau,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "document", required = false) MultipartFile document){
        try {
            Cours cours = new Cours();
            cours.setTitre(titre);
            cours.setDescription(description);
            cours.setAuteur(auteur);
            cours.setCategorie(categorie);
            cours.setLangue(langue);
            cours.setDuree(duree);
            cours.setPrix(prix);
            cours.setNiveau(niveau);

            if (image != null) {
                String imageUrl = coursService.saveImage(image);
                cours.setImageUrl(imageUrl);
            }

            if (document != null) {
                String docUrl = coursService.saveDocument(document);
                cours.setDocumentUrl(docUrl);
            }

            Cours savedCours = coursService.addCours(cours);
            return ResponseEntity.ok(savedCours);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/addCoursTestCert")
    public ResponseEntity<Cours> addCours(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("auteur") String auteur,
            @RequestParam("categorie") String categorie,
            @RequestParam("langue") String langue,
            @RequestParam("duree") int duree,
            @RequestParam("prix") Double prix,
            @RequestParam("niveau") NiveauCours niveau,
            @RequestParam("certNom") String certNom,
            @RequestParam("certInstructeur") String certInstructeur,
            @RequestParam("certValidite") int certValidite,
            @RequestParam("certNiveau") NiveauCertificat certNiveau,
            @RequestParam("testContenu") String testContenu,
            @RequestParam("testScore") double testScore,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "document", required = false) MultipartFile document){
        try {
            Cours cours = new Cours();
            cours.setTitre(titre);
            cours.setDescription(description);
            cours.setAuteur(auteur);
            cours.setCategorie(categorie);
            cours.setLangue(langue);
            cours.setDuree(duree);
            cours.setPrix(prix);
            cours.setNiveau(niveau);

            if (image != null) {
                String imageUrl = coursService.saveImage(image);
                cours.setImageUrl(imageUrl);
            }

            if (document != null) {
                String docUrl = coursService.saveDocument(document);
                cours.setDocumentUrl(docUrl);
            }
            // Création du certificat
            Certificat certificat = new Certificat();
            certificat.setNom(certNom);
            certificat.setInstructeur(certInstructeur);
            certificat.setValidite(certValidite);
            certificat.setNiveau(certNiveau);

            // Création du test
            Test test = new Test();
            test.setDescription(testContenu);
            test.setScore(testScore);

            // Associer le certificat et le test au cours
            cours.setCertificat(certificat);
            cours.setTest(test);

            Cours savedCours = coursService.addCours(cours);
            return ResponseEntity.ok(savedCours);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/updateCours")
    public ResponseEntity<Cours> updateCours(
            @RequestParam("id") Long id,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("auteur") String auteur,
            @RequestParam("categorie") String categorie,
            @RequestParam("langue") String langue,
            @RequestParam("duree") int duree,
            @RequestParam("prix") Double prix,
            @RequestParam("niveau") NiveauCours niveau,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "document", required = false) MultipartFile document){
        try {
            Cours cours = new Cours();
            cours.setId(id);
            cours.setTitre(titre);
            cours.setDescription(description);
            cours.setAuteur(auteur);
            cours.setCategorie(categorie);
            cours.setLangue(langue);
            cours.setDuree(duree);
            cours.setPrix(prix);
            cours.setNiveau(niveau);

            if (image != null) {
                String imageUrl = coursService.saveImage(image);
                cours.setImageUrl(imageUrl);
            }

            if (document != null) {
                String docUrl = coursService.saveDocument(document);
                cours.setDocumentUrl(docUrl);
            }

            Cours savedCours = coursService.updateCours(cours);
            return ResponseEntity.ok(savedCours);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
