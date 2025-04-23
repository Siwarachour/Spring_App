package tn.esprit.back.Repository.coursCertificat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.coursCertificat.Certificat;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {
}
