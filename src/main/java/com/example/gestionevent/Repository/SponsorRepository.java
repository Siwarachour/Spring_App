package com.example.gestionevent.Repository;

import com.example.gestionevent.entities.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor,Long> {
}
