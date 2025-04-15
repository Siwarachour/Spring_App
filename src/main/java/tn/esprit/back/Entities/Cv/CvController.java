package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    // Method to add CV (already existing)
    @PostMapping("/add")
    public Integer addCv(@RequestBody Cv cv ) {
        return (Integer) cvService.addCv(cv);
    }
    @PostMapping("/uploadPhoto/{cvId}")
    public ResponseEntity<String> uploadCvPhoto(@PathVariable int cvId, @RequestParam("file") MultipartFile file) {
        try {
            // Define the path where the photo will be stored
            String uploadDir = "C:/Users/21650/Desktop/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";

            // Generate a unique filename based on the CV ID
            String fileName = "photo_" + cvId + ".jpg";
            Path filePath = Paths.get(uploadDir, fileName);

            // Save the file to the disk
            file.transferTo(filePath);

            // Return the file URL as a response
            String photoUrl = "/uploads/" + fileName;  // Update the URL based on your application

            return ResponseEntity.ok(photoUrl);  // Return photo URL directly
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }


    // Method for downloading the CV (already existing)
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

    // Method to fetch CV by username (already existing)
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

    // New method for viewing CV as PDF (not downloading)
    @GetMapping("/view/{id}")
    public ResponseEntity<InputStreamResource> viewCv(@PathVariable int id) {
        try {
            // Call the service method
            return cvService.getCvPdfForViewing(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


}
