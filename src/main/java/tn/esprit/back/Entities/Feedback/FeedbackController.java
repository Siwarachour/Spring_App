package tn.esprit.back.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Feedback.Feedback;
import tn.esprit.back.Entities.Feedback.FeedbackService;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Feedback updateFeedback(@PathVariable Integer id, @RequestBody Feedback feedback) {
        return feedbackService.updateFeedback(id, feedback);
    }
}
