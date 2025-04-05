package com.example.gestionevent.Controllers;

import com.example.gestionevent.Services.ISponsorService;
import com.example.gestionevent.Services.SponsorServiceImplement;
import com.example.gestionevent.entities.Sponsor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/Sponsor")
@CrossOrigin(origins = "http://localhost:4200")
public class SponsorController {

    @Autowired
    private ISponsorService sponsorService;

    @PostMapping("/addSponsor")
    public Sponsor addSponsor(@RequestBody Sponsor sponsor) {
        return sponsorService.addSponsor(sponsor);
    }

    @PutMapping(value = "/updateSponsor/{idSponsor}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSponsor(
            @PathVariable Long idSponsor,
            @RequestPart("sponsor") String sponsorJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Sponsor sponsorDetails = objectMapper.readValue(sponsorJson, Sponsor.class);

            // Gestion du fichier
            if (file != null && !file.isEmpty()) {
                String fileName = ((SponsorServiceImplement)sponsorService).storeLogo(file);
                sponsorDetails.setLogo(fileName);
            } else {
                // Conserver l'ancien logo si aucun nouveau fichier n'est fourni
                Sponsor existingSponsor = sponsorService.getSponsorbyId(idSponsor);
                sponsorDetails.setLogo(existingSponsor.getLogo());
            }

            Sponsor updatedSponsor = sponsorService.updateSponsor(idSponsor, sponsorDetails);
            return ResponseEntity.ok(updatedSponsor);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating sponsor: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteSponsor/{idSponsor}")
    public void deleteSponsor(@PathVariable long idSponsor) {
        sponsorService.deleteSponsor(idSponsor);
    }

    @GetMapping("/retriveAllSponsors")
    public List<Sponsor> retriveAllSponsors() {
        return sponsorService.retriveAllSponsors();
    }

    @GetMapping("/retriveSponsor/{idSponsor}")
    public Sponsor getSponsorbyId(@PathVariable long idSponsor) {
        return sponsorService.getSponsorbyId(idSponsor);
    }

    @PostMapping(value = "/uploadLogo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        return ((SponsorServiceImplement)sponsorService).storeLogo(file);
    }
    private String storeImage(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Paths.get(uploadDir + fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }
}