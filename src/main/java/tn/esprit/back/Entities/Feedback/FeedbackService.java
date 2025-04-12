package tn.esprit.back.Entities.Feedback;

import org.springframework.stereotype.Service;


@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback updateFeedback(Integer id, Feedback feedback) {
        Feedback existingFeedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        existingFeedback.setNote(feedback.getNote());  // Update the rating
        existingFeedback.setCommentaire(feedback.getCommentaire());  // Optionally, update the comment
        return feedbackRepository.save(existingFeedback);
    }
}
