package repository;

import models.Offre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreRepo extends JpaRepository<Offre, Long> {
}
