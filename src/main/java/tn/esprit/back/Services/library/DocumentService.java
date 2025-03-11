package tn.esprit.back.Services.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Repository.library.DocumentRepository;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService implements IDocument {

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public Document addDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document updateDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(long idDocument) {
        documentRepository.deleteById(idDocument);
    }

    @Override
    public List<Document> getAllDocument() {
        return documentRepository.findAll();
    }

    @Override
    public Document getDocumentById(long idDocument) {
        Optional<Document> document = documentRepository.findById(idDocument);
        return document.orElse(null);
    }
}
