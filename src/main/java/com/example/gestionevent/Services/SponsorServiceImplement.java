package com.example.gestionevent.Services;

import com.example.gestionevent.Repository.SponsorRepository;
import com.example.gestionevent.entities.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class SponsorServiceImplement implements ISponsorService {

    @Autowired
    SponsorRepository sponsorRepository;


    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public Sponsor addSponsor(Sponsor sponsor) {
        return sponsorRepository.save(sponsor);
    }

    @Override
    public Sponsor updateSponsor(Long idSponsor, Sponsor sponsorDetails) {
        Sponsor sponsor = sponsorRepository.findById(idSponsor)
                .orElseThrow(() -> new RuntimeException("Sponsor not found with id: " + idSponsor));

        // Mettre à jour uniquement les champs non nuls
        if (sponsorDetails.getNomSponsor() != null) {
            sponsor.setNomSponsor(sponsorDetails.getNomSponsor());
        }
        if (sponsorDetails.getDescriptionSponsor() != null) {
            sponsor.setDescriptionSponsor(sponsorDetails.getDescriptionSponsor());
        }
        if (sponsorDetails.getContact() != null) {
            sponsor.setContact(sponsorDetails.getContact());
        }

        if (sponsorDetails.getLogo() != null) {
            sponsor.setLogo(sponsorDetails.getLogo());
        }

        return sponsorRepository.save(sponsor);
    }

    @Override
    public void deleteSponsor(long idSponsor) {
        Sponsor sponsor = sponsorRepository.findById(idSponsor)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));

        // Supprimer le fichier logo si existant
        if(sponsor.getLogo() != null && !sponsor.getLogo().isEmpty()) {
            try {
                Path filePath = Paths.get(UPLOAD_DIR + sponsor.getLogo());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete logo file", e);
            }
        }

        sponsorRepository.delete(sponsor);
    }

    @Override
    public List<Sponsor> retriveAllSponsors() {
        return sponsorRepository.findAll();
    }

    @Override
    public Sponsor getSponsorbyId(long idSponsor) {
        return sponsorRepository.findById(idSponsor)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
    }

    public String storeLogo(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

}