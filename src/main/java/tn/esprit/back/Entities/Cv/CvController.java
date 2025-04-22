package tn.esprit.back.Entities.Cv;

import io.netty.util.internal.logging.InternalLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.back.Repository.User.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;
    private final CvRepo cvRepo;
    private final UserRepository userRepository;

    // Method to add CV (already existing)
    @PostMapping("/add")
    public Object addCv(@RequestBody Cv cv) {  // Make sure @RequestBody is used if expecting a JSON body
        System.out.println("Received CV data: " + cv);  // Debug the incoming CV object

        cv.setStudent(userRepository.findByusername("siwar23"));
        // Save the CV to the database
        Cv savedCv = cvRepo.save(cv);
        System.out.println("Saved CV with ID: " + savedCv.getId());  // Debug the saved CV ID

        try {
            // Generate PDF and save it
            String fileName = CvPdfGenerator.generateAndSavePdf(savedCv);
            System.out.println("PDF generated with file name: " + fileName);  // Debug the file name

            // Create download link
            String downloadLink = "http://localhost:8089/cv/download/" + savedCv.getId();
            savedCv.setPdfDownloadLink(downloadLink);
            System.out.println("Generated download link: " + downloadLink);  // Debug the download link

            // Save the CV again with the download link
            cvRepo.save(savedCv);
            System.out.println("Updated CV saved with download link");  // Debug the second save

            return savedCv.getId();  // or return downloadLink if needed
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error generating PDF: " + e.getMessage());  // Debug the error message
            throw new RuntimeException("Failed to generate CV PDF");
        }
    }



    @PostMapping("/uploadPhoto/{cvId}")
    public ResponseEntity<String> uploadCvPhoto(@PathVariable int cvId, @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Received upload request for CV ID: " + cvId);
            System.out.println("Original file name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");

            // Ensure the directory exists
            String uploadDir = "D:/doc/Bureau/NOUVEAU/Back/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/photo";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Upload directory created: " + created);
            }

            // Generate file name
            String fileName = "photo_" + cvId + ".jpg";
            Path filePath = Paths.get(uploadDir, fileName);

            // Log path before transferring the file
            System.out.println("Saving file to: " + filePath.toString());
            file.transferTo(filePath.toFile());

            System.out.println("File saved successfully to: " + filePath.toString());

            // Return the photo URL
            String photoUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(photoUrl);

        } catch (IOException e) {
            System.out.println("File upload failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }



    @PutMapping("/updatePhotoUrl")
    public ResponseEntity<?> updatePhotoUrl(@RequestBody Cv cv) {
        Optional<Cv> optionalCv = cvRepo.findById(cv.getId());
        if(optionalCv.isPresent()) {
            Cv existingCv = optionalCv.get();
            existingCv.setPhotoUrl(cv.getPhotoUrl());
            cvRepo.save(existingCv);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CV not found");
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
        String uploadDir = "D:/doc/Bureau/NOUVEAU/Back/Spring_App/src/main/java/tn/esprit/back/Entities/Cv/uploads/";
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
