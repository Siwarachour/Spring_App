package tn.esprit.back.Services.library;
import tn.esprit.back.Entities.library.Document;
import java.util.List;


public interface IDocument {

    Document addDocument(Document document);
    Document updateDocument(Document document);
    void deleteDocument(long idDocument );
    List<Document> getAllDocument();
    Document getDocumentById(long idDocument);
}
