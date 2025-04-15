package tn.esprit.back.Entities.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Feedback.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
