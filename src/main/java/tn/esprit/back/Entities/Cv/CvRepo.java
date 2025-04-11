package tn.esprit.back.Entities.Cv;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.User.User;

public interface CvRepo extends JpaRepository<Cv, Integer> {
    Cv findByStudent(User user);

}
