package tn.esprit.back.Repository.diploma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.University.University;

@Repository
public interface UniversityRepository extends JpaRepository<University,Long> {
}
