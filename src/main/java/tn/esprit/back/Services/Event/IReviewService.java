package tn.esprit.back.Services.Event;


import tn.esprit.back.Entities.Event.Review;

import java.util.List;

public interface IReviewService {
    Review addReview (Review Review);
    Review updateReview (Review Review);
    void deleteReview (long idReview);
    List<Review> retriveAllReviews ();
    Review getEquipebyId  (long idReview);

    List<Review> getReviewsByEventId(Long idEvent);
}
