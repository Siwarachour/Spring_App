package tn.esprit.back.Repository.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.Event.Event;
import tn.esprit.back.Entities.Event.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("SELECT r FROM Reservation r WHERE r.event.idEvent = :eventId")
    List<Reservation> findByEventId(@Param("eventId") Long eventId);
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.sponsors WHERE e.idEvent = :id")
    Optional<Event> findByIdWithSponsors(@Param("id") Long id);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.reviews WHERE e.idEvent = :id")
    Optional<Event> findByIdWithReviews(@Param("id") Long id);

    @Query("SELECT SUM(r.nombrePlaces) FROM Reservation r WHERE r.event.idEvent = :eventId")
    Integer sumReservedSeatsByEventId(@Param("eventId") Long eventId);
    @Query("SELECT r FROM Reservation r JOIN FETCH r.event WHERE r.idReservation = :id")
    Optional<Reservation> findByIdWithEvent(@Param("id") Long id);

}
