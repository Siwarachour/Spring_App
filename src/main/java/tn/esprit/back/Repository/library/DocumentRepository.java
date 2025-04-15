package tn.esprit.back.Repository.library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.DocumentStatus;


@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    int countByStudentIdAndStatus(Long studentId, DocumentStatus status);

}
