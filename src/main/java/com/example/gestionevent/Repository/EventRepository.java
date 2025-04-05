package com.example.gestionevent.Repository;

import com.example.gestionevent.entities.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    @EntityGraph(attributePaths = {"sponsors"})
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.sponsors")
    List<Event> findAllWithSponsors();
    @EntityGraph(attributePaths = {"reviews"})
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.reviews WHERE e.idEvent = :id")
    Optional<Event> findByIdWithReviews(@Param("id") Long idEvent);


}
