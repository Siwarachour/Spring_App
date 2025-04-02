package tn.esprit.back.Repository.coursCertificat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.coursCertificat.Test;

@Repository
public interface TestRepository extends JpaRepository<Test,Long> {
}
