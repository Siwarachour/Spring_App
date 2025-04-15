package tn.esprit.back.Entities.Offre;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController

@RequestMapping("/offre")
@RequiredArgsConstructor
public class OffreController {

    private final OffreService offreService;

    // Correct the method name to reflect the operation being performed

    @PostMapping("/add")
    public ResponseEntity<?> addOffre(@RequestParam("offre") String offreJson, @RequestParam("image") MultipartFile image) throws JsonProcessingException {
        // Deserialize the 'offre' JSON string into your Offre object
        Offre offre = new ObjectMapper().readValue(offreJson, Offre.class);

        // Call the service method to add the offer and get the generated ID
        Long offreId = offreService.addOffre(offre, image);

        // Return the ID of the newly created offer in the response body
        return ResponseEntity.ok(offreId);
    }






    // GET: Fetch all Offres
    @GetMapping("/all")
    public ResponseEntity<List<Offre>> getAllOffres() {
        List<Offre> offres = offreService.getAllOffres();
        return ResponseEntity.ok(offres);
    }

    // GET: Fetch an Offre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Offre> getOffreById(@PathVariable Long id) {
        Offre offre = offreService.getOffreById(id);
        if (offre == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if not found
        }
        return ResponseEntity.ok(offre);
    }

    // PUT: Edit an existing Offre
    @PutMapping("/edit/{id}")
    public ResponseEntity<Offre> editOffre(@PathVariable Long id, @RequestBody Offre updatedOffre) {
        Offre offre = offreService.editOffre(id, updatedOffre);
        if (offre == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if not found
        }
        return ResponseEntity.ok(offre);  // Return the updated offer
    }

    // DELETE: Remove an Offre
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
        boolean isDeleted = offreService.deleteOffre(id);
        if (!isDeleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 if not found
        }
        return ResponseEntity.noContent().build();  // Return 204 if successful
    }
}
