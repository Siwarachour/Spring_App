package tn.esprit.back.Repository.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.Event.Sponsor;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor,Long> {
}
