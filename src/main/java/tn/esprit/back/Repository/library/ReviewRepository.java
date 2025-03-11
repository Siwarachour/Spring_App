package tn.esprit.back.Repository.library;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.library.Review;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
}
