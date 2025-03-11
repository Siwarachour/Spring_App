package tn.esprit.back.Controllers.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.library.Document;
import tn.esprit.back.Services.library.IDocument;
import java.util.List;


@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    IDocument documentService;

    @PutMapping("/update")
    public Document updateDocument(@RequestBody Document document) {
        return documentService.updateDocument(document);
    }

    @PostMapping("/add")
    public Document addDocument(@RequestBody Document document) {
        return documentService.addDocument(document);
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
}
