package com.example.gestionevent.Services;

import com.example.gestionevent.entities.Review;

import java.util.List;

public interface IReviewService {
    Review addReview (Review Review);
    Review updateReview (Review Review);
    void deleteReview (long idReview);
    List<Review> retriveAllReviews ();
    Review getEquipebyId  (long idReview);

    List<Review> getReviewsByEventId(Long idEvent);
}
