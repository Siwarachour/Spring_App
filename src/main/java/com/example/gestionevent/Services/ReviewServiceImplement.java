package com.example.gestionevent.Services;

import com.example.gestionevent.Repository.ReviewRepository;
import com.example.gestionevent.entities.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImplement implements IReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review Review) {
        return reviewRepository.save(Review);
    }

    @Override
    public Review updateReview(Review Review) {
        return reviewRepository.save(Review);
    }

    @Override
    public void deleteReview(long idReview) {
        reviewRepository.deleteById(idReview);
    }

    @Override
    public List<Review> retriveAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getEquipebyId(long idReview) {
        return reviewRepository.findById(idReview).get();
    }
    @Override
    public List<Review> getReviewsByEventId(Long idEvent) {
        return reviewRepository.findByEventIdEvent(idEvent);
    }
}
