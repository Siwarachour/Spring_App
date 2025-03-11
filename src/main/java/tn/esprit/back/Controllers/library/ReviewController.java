package tn.esprit.back.Controllers.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.library.Review;
import tn.esprit.back.Services.library.IReview;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    IReview reviewService;


    @PutMapping("/update")
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @PostMapping("/add")
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping("/getall")
    public List<Review> getAllReview() {
        return reviewService.getAllReview();
    }


    @GetMapping("/retrieve/{idReview}")
    public Review getReviewById(@PathVariable long idReview) {
        return reviewService.getReviewById(idReview);
    }

    @DeleteMapping("/delete/{idReview}")
    public void deleteReview(@PathVariable long idReview) {
        reviewService.deleteReview(idReview);
    }
}
