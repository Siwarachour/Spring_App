package tn.esprit.back.Controllers.library;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.DocumentStatus;
import tn.esprit.back.Entities.library.DocumentType;
import tn.esprit.back.Services.library.IDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/document")
public class DocumentController {


    @Autowired
    IDocument documentService;

    private static final String UPLOAD_DIR = "uploads/";

    @PutMapping("/update")
    public Document updateDocument(@RequestBody Document document) {
        return documentService.updateDocument(document);
    }
    @GetMapping("/getall")
    public List<Document> getAllDocument() {
        return documentService.getAllDocument();
    }
    @GetMapping("/retrieve/{idDocument}")
    public Document getDocumentById(@PathVariable long idDocument) {
        return documentService.getDocumentById(idDocument);
    }
    @DeleteMapping("/delete/{idDocument}")
    public void deleteDocument(@PathVariable long idDocument) {
        documentService.deleteDocument(idDocument);
    }

    @PostMapping("/add")
    public ResponseEntity<Document> addDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("documentType") String documentType,
            @RequestParam("keywords") String keywords,
            @RequestParam("status") String status,
            @RequestParam("categoryIds") List<Long> categoryIds) throws IOException {

        // File handling
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Document creation
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setDocumentType(DocumentType.valueOf(documentType));
        document.setKeywords(keywords);
        document.setStatus(DocumentStatus.valueOf(status));
        document.setFileUrl(filePath.toString());

        Document savedDocument = documentService.addDocument(document);

        // Assign categories
        categoryIds.forEach(categoryId -> {
            affectCategoryToDocument(savedDocument.getIdDocument(), categoryId);
        });

        return ResponseEntity.ok(savedDocument);
    }


    @PostMapping("/addWithUser/{userId}")
    public Document addDocumentWithUser(@RequestBody Document document, @PathVariable Long userId) {
        return documentService.addDocumentWithUser(document, userId);
    }
    @PutMapping("/affectReview/{idDocument}/{idReview}")
    public Document affectReviewToDocument(@PathVariable Long idDocument, @PathVariable Long idReview) {
        return documentService.assignReviewToDocument(idDocument, idReview);
    }
    @PutMapping("/affectCategory/{idDocument}/{idCategory}")
    public Document affectCategoryToDocument(@PathVariable Long idDocument, @PathVariable Long idCategory) {
        return documentService.addDocumentToCategory(idDocument, idCategory);
    }
}
