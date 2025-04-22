package tn.esprit.back.Entities.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.Feedback.Feedback;
@Repository

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
