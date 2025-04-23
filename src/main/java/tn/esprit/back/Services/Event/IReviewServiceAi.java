package tn.esprit.back.Services.Event;


import tn.esprit.back.Entities.Event.Review;

import java.util.List;
import java.util.Map;

public interface IReviewServiceAi {
    public Map<String, Object> analyzeReviews(List<Review> reviews) ;

    Map<String, Object> analyzeReviewsByEvent(Long eventId);
    Map<String, Object> analyzeAllReviews();
    }
