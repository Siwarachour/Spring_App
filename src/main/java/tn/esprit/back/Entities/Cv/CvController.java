package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    @PostMapping("/add")
    public Integer addCv(@RequestBody Cv cv) {
        return (Integer) cvService.addCv(cv);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Object> downloadCv(@PathVariable int id) throws Exception {
        Cv cv = cvService.getCvById(id); // Make sure this method exists in your service
        if (cv == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = "cv_" + cv.getId() + ".pdf";
        String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }

    // New endpoint to fetch CV by username

    @GetMapping("/user/{username}")
    public ResponseEntity<Object> getCvByUsername(@PathVariable String username) {
        try {
            Cv cv = cvService.findCvByUsername(username); // Find CV by username
            if (cv == null) {
                return ResponseEntity.notFound().build(); // Return 404 if CV not found
            }
            return ResponseEntity.ok(cv); // Return 200 with CV if found
        } catch (RuntimeException e) {
            // Return a 500 status with the error message if an exception occurs
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
