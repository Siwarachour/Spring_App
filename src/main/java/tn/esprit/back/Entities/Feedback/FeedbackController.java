package tn.esprit.back.Entities.Feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Feedback.Feedback;
import tn.esprit.back.Entities.Feedback.FeedbackRepository;
import tn.esprit.back.Entities.Feedback.FeedbackService;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PutMapping("/update")
    public ResponseEntity<Feedback> updateFeedback(@RequestBody Feedback feedback) {
        Feedback updated = feedbackRepository.save(feedback);
        return ResponseEntity.ok(updated);
    }
}
