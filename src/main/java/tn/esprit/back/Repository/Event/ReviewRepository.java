package tn.esprit.back.Repository.Event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.Event.Review;

import java.util.List;

@Repository

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByEventIdEvent(Long idEvent);
}
