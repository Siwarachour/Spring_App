package tn.esprit.back.Controllers.Event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Event.Review;
import tn.esprit.back.Services.Event.IReviewService;
import tn.esprit.back.Services.Event.IReviewServiceAi;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/Review")

public class ReviewController {

    @Autowired
    IReviewService reviewService;
    @Autowired
    private IReviewServiceAi reviewServiceAi;





    @PostMapping(value = "/addReview",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }
    @PutMapping("/updateReview")
    Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("/retriveAllReview")
    List<Review> retriveAllReview() {
        return reviewService.retriveAllReviews();
    }

    @GetMapping("/retriveReview/{idReview}")
    Review getReviewById(@PathVariable long idReview) {
        return reviewService.getEquipebyId(idReview);
    }

    @DeleteMapping("/deleteReview/{idReview}")
    void deleteReview(@PathVariable long idReview) {
        reviewService.deleteReview(idReview);
    }
    @GetMapping("/byEvent/{idEvent}")
    public List<Review> getReviewsByEvent(@PathVariable Long idEvent) {
        return reviewService.getReviewsByEventId(idEvent);
    }
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeReviews(@RequestBody List<Review> reviews) {
        Map<String, Object> result = reviewServiceAi.analyzeReviews(reviews);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/analyze/event/{eventId}")
    public ResponseEntity<?> analyzeEventReviews(@PathVariable Long eventId) {
        Map<String, Object> result = reviewServiceAi.analyzeReviewsByEvent(eventId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analyze/all")
    public ResponseEntity<?> analyzeAllReviews() {
        Map<String, Object> result = reviewServiceAi.analyzeAllReviews();
        return ResponseEntity.ok(result);
    }

}
