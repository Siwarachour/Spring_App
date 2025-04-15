package tn.esprit.back.Entities.Application;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Cv.Cv;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {

}
