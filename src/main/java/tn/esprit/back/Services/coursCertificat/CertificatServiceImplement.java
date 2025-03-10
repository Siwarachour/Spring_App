package tn.esprit.back.Services.coursCertificat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.coursCertificat.Certificat;
import tn.esprit.back.Repository.coursCertificat.CertificatRepository;

import java.util.List;

@Service
public class CertificatServiceImplement implements ICertificatService {

    @Autowired
    CertificatRepository certificatRepository;

    @Override
    public Certificat addCertificat(Certificat certificat) {
        return certificatRepository.save(certificat);
    }

    @Override
    public Certificat updateCertificat(Certificat certificat) {
        return certificatRepository.save(certificat);
    }

    @Override
    public void deleteCertificat(long idcertificat) {
        certificatRepository.deleteById(idcertificat);

    }

    @Override
    public List<Certificat> getAllCertificat() {
        return certificatRepository.findAll();
    }

    @Override
    public Certificat getCertificatById(long idcertificat) {
        return certificatRepository.findById(idcertificat).get();
    }
}
