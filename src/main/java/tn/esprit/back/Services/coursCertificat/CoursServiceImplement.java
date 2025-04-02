package tn.esprit.back.Services.coursCertificat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.coursCertificat.Certificat;
import tn.esprit.back.Entities.coursCertificat.Cours;
import tn.esprit.back.Entities.coursCertificat.Test;
import tn.esprit.back.Repository.coursCertificat.CertificatRepository;
import tn.esprit.back.Repository.coursCertificat.CoursRepository;
import tn.esprit.back.Repository.coursCertificat.TestRepository;

import java.util.List;

@Service
public class CoursServiceImplement implements ICoursService {

    @Autowired
    CoursRepository coursRepository;
    @Autowired
    CertificatRepository certificatRepository;
    @Autowired
    TestRepository testRepository;

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

    @Override
    public Cours affectCertificatToCours(long idCours, long idCertificat) {
        Cours cours = coursRepository.findById(idCours).get();
        Certificat certificat =certificatRepository.findById(idCertificat).get();
        cours.setCertificat(certificat);
        return coursRepository.save(cours);
    }

    @Override
    public Cours addCoursAndAffectCertificat(Cours cours, long idCertificat) {
        Certificat C =certificatRepository.findById(idCertificat).get();
        cours.setCertificat(C);
        return coursRepository.save(cours);
    }

    @Override
    public Cours affectTestToCours(long idCours, long idTest) {
        Cours cours = coursRepository.findById(idCours).get();
        Test test =testRepository.findById(idTest).get();
        cours.setTest(test);
        return coursRepository.save(cours);
    }

    @Override
    public Cours addCoursAndAffectTest(Cours cours, long idTest) {
        Test t =testRepository.findById(idTest).get();
        cours.setTest(t);
        return coursRepository.save(cours);
    }
}
