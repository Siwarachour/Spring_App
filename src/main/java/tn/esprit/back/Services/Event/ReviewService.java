package tn.esprit.back.Services.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.back.Entities.Event.Review;
import tn.esprit.back.Repository.Event.ReviewRepository;

import java.util.*;
import java.util.stream.Collectors;
@Service

public class ReviewService implements IReviewServiceAi {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    ReviewRepository reviewRepository;

    public Map<String, Object> analyzeReviews(List<Review> reviews) {
        String pythonUrl = "http://localhost:8000/analyze";

        // Préparer les données pour l'API Python
        List<Map<String, Object>> reviewData = reviews.stream()
                .map(review -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("idReview", review.getIdReview());
                    data.put("rating", review.getRating());
                    data.put("commentaire", review.getCommentaire());
                    data.put("dateReview", review.getDateReview().toString());

                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("idEvent", review.getEvent().getIdEvent());
                    eventData.put("nomEvent", review.getEvent().getNomEvent());
                    data.put("event", eventData);

                    return data;
                })
                .collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(reviewData, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, request, Map.class);

        return response.getBody();
    }

    public Map<String, Object> analyzeReviewsByEvent(Long eventId) {
        List<Review> reviews = reviewRepository.findByEventIdEvent(eventId);
        return analyzeReviews(reviews);
    }

    public Map<String, Object> analyzeAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return analyzeReviews(reviews);
    }
}