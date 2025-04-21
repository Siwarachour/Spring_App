package tn.esprit.back.Entities.Application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.User.User;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {

    @Query("SELECT a.student FROM Application a WHERE a.status = tn.esprit.back.Entities.Application.ApplicationStatus.ACCEPTED")
    List<User> findUsersWithAcceptedApplications();
}
