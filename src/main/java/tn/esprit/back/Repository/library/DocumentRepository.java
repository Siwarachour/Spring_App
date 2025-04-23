package tn.esprit.back.Repository.library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.enums.Status;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.DocumentStatus;

import java.util.List;


@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    int countByStudentIdAndStatus(Long studentId, DocumentStatus status);

    List<Document> findByStatus(DocumentStatus status);
}
