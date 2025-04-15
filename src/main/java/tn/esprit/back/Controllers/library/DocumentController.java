package tn.esprit.back.Controllers.library;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Entities.library.DocumentStatus;
import tn.esprit.back.Entities.library.DocumentType;
import tn.esprit.back.Repository.library.DocumentRepository;
import tn.esprit.back.Services.library.IDocument;
import tn.esprit.back.Services.library.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/document")
public class DocumentController {


    @Autowired
    IDocument documentService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private DocumentRepository documentRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> updateDocument(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("idDocument") Long idDocument, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("documentType") String documentType, @RequestParam("keywords") String keywords, @RequestParam("status") String status, @RequestParam("categoryIds") List<Long> categoryIds) throws IOException {
        // Retrieve the existing document
        Document document = documentService.getDocumentById(idDocument);
        document.setTitle(title);
        document.setDescription(description);
        document.setDocumentType(DocumentType.valueOf(documentType));
        document.setKeywords(keywords);
        document.setStatus(DocumentStatus.valueOf(status));

        // If a new file is provided, handle file storage and update fileUrl.
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            document.setFileUrl(filePath.toString());
        }
        // Update categories (you may need to call a service method to assign categories)
        categoryIds.forEach(categoryId -> {
            // For example, you could call documentService.addDocumentToCategory(document.getIdDocument(), categoryId);
        });

        Document updatedDocument = documentService.updateDocument(document);
        return ResponseEntity.ok(updatedDocument);
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
    public ResponseEntity<Document> addDocument(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("documentType") String documentType, @RequestParam("keywords") String keywords, @RequestParam("status") String status, @RequestParam("categoryIds") List<Long> categoryIds) {
        // Upload the file to Cloudinary
        String fileUrl = cloudinaryService.uploadFile(file, "documents");  // Customize folder name if needed
        if (fileUrl == null) {
            // Return a failure response, but the body must still be a Document (with appropriate error handling)
            Document errorDocument = new Document();
            errorDocument.setTitle("File upload failed");
            return ResponseEntity.badRequest().body(errorDocument);
        }

        // Document creation
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setDocumentType(DocumentType.valueOf(documentType));
        document.setKeywords(keywords);
        document.setStatus(DocumentStatus.valueOf(status));
        document.setFileUrl(fileUrl);  // Use the Cloudinary URL instead of local file path

        // Save the document
        Document savedDocument = documentRepository.save(document);

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

    @GetMapping("/{id}/text")
    public ResponseEntity<String> extractText(@PathVariable Long id) {
        Optional<Document> doc = documentRepository.findById(id);
        if (doc.isPresent()) {
            String fileUrl = doc.get().getFileUrl();
            try (InputStream input = new URL(fileUrl).openStream();
                 PDDocument pdf = PDDocument.load(input)) {

                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdf);
                return ResponseEntity.ok(text);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error extracting PDF text: " + e.getMessage());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/approved-count")
    public int getApprovedDocumentsCount(@PathVariable Long userId) {
        return documentService.countApprovedDocumentsByUser(userId);
    }
}

