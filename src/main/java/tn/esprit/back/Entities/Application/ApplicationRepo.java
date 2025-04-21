package tn.esprit.back.Entities.Application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.Cv.Cv;
@Repository
public interface ApplicationRepo extends JpaRepository<Application, Integer> {

}
