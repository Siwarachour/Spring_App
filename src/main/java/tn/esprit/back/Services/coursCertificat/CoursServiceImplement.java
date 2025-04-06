package tn.esprit.back.Services.coursCertificat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.coursCertificat.Certificat;
import tn.esprit.back.Entities.coursCertificat.Cours;
import tn.esprit.back.Entities.coursCertificat.Test;
import tn.esprit.back.Repository.coursCertificat.CertificatRepository;
import tn.esprit.back.Repository.coursCertificat.CoursRepository;
import tn.esprit.back.Repository.coursCertificat.TestRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CoursServiceImplement implements ICoursService {

    @Autowired
    CoursRepository coursRepository;
    @Autowired
    CertificatRepository certificatRepository;
    @Autowired
    TestRepository testRepository;

    private static final String UPLOAD_DIR = "C:/xampp/htdocs/piARCTIC/";

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


    //pour l'image
    @Override
    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }


    //pour le pdf
    @Override
    public String saveDocument(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }


}
