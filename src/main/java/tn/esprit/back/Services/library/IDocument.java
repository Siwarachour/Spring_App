package tn.esprit.back.Services.library;
import tn.esprit.back.Entities.library.Document;
import java.util.List;


public interface IDocument {

    Document addDocument(Document document);
    Document updateDocument(Document document);

    void deleteDocument(long idDocument );
    List<Document> getAllDocument();
    Document getDocumentById(long idDocument);

    Document addDocumentWithUser(Document document, Long userId);

    //Document assignReviewToDocument(Long documentId, Long reviewId);

    Document addDocumentToCategory(Long documentId, Long categoryId);

    int countApprovedDocumentsByUser(Long userId);

    Document approveDocument(long idDocument);

}
