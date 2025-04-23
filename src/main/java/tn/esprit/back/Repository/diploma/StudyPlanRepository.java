package tn.esprit.back.Repository.diploma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.StudyPlan.StudyPlan;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan,Long> {
}
