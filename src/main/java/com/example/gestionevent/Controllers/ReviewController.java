package com.example.gestionevent.Controllers;

import com.example.gestionevent.Services.IReviewService;
import com.example.gestionevent.entities.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Review")
public class ReviewController {
    @Autowired
    IReviewService reviewService;


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
}
