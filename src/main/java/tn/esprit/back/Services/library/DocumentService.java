package tn.esprit.back.Services.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.library.Category;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.Review;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Repository.library.CategoryRepository;
import tn.esprit.back.Repository.library.DocumentRepository;
import tn.esprit.back.Repository.library.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService implements IDocument {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;

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
        Document document = documentRepository.findById(idDocument)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Remove associations with categories
        for (Category category : document.getCategories()) {
            category.getDocuments().remove(document);
        }
        document.getCategories().clear();

        documentRepository.delete(document);
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



    @Override
    public Document addDocumentWithUser(Document document, Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        document.setStudent(user);
        return documentRepository.save(document);
    }
    @Override
    public Document assignReviewToDocument(Long documentId, Long reviewId) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        document.setReview(review);
        review.setDocument(document);
        reviewRepository.save(review);
        return documentRepository.save(document);
    }
    @Override
    public Document addDocumentToCategory(Long documentId, Long categoryId) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        document.getCategories().add(category);
        return documentRepository.save(document);
    }
}
