package tn.esprit.back.Services.coursCertificat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.coursCertificat.Cours;
import tn.esprit.back.Repository.coursCertificat.CoursRepository;

import java.util.List;

@Service
public class CoursServiceImplement implements ICoursService {

    @Autowired
    CoursRepository coursRepository;

    @Override
    public Cours addCours(Cours cours) {
        return coursRepository.save(cours);
    }

    @Override
    public Cours updateCours(Cours cours) {
        return coursRepository.save(cours);
    }

    @Override
    public void deleteCours(long idcours) {
        coursRepository.deleteById(idcours);

    }

    @Override
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    @Override
    public Cours getCoursById(long idcours) {
        return coursRepository.findById(idcours).get();
    }
}
