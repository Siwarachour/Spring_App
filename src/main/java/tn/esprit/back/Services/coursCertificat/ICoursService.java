package tn.esprit.back.Services.coursCertificat;

import tn.esprit.back.Entities.coursCertificat.Cours;

import java.util.List;

public interface ICoursService {
    Cours addCours(Cours cours);
    Cours updateCours(Cours cours);
    void deleteCours(long idcours);
    List<Cours> getAllCours();
    Cours getCoursById(long idcours);
}
