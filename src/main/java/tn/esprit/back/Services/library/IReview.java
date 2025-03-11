package tn.esprit.back.Services.library;
import tn.esprit.back.Entities.library.Review;
import java.util.List;

public interface IReview {

    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(long idReview );
    List<Review> getAllReview();
    Review getReviewById(long idReview);
}
