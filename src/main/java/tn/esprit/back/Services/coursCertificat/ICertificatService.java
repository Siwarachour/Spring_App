package tn.esprit.back.Services.coursCertificat;

import tn.esprit.back.Entities.coursCertificat.Certificat;

import java.util.List;

public interface ICertificatService {
    Certificat addCertificat(Certificat certificat);
    Certificat updateCertificat(Certificat certificat);
    void deleteCertificat(long idcertificat);
    List<Certificat> getAllCertificat();
    Certificat getCertificatById(long idcertificat);
}
