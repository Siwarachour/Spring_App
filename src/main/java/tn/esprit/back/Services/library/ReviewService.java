package tn.esprit.back.Services.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.library.Review;
import tn.esprit.back.Repository.library.ReviewRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService implements IReview {

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(long idReview) {
        reviewRepository.deleteById(idReview);
    }

    @Override
    public List<Review> getAllReview() {
        return (List<Review>) reviewRepository.findAll();
    }

    @Override
    public Review getReviewById(long idReview) {
        Optional<Review> review = reviewRepository.findById(idReview);
        return review.orElse(null);
    }
}
