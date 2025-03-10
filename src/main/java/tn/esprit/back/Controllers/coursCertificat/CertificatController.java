package tn.esprit.back.Controllers.coursCertificat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.coursCertificat.Certificat;
import tn.esprit.back.Services.coursCertificat.ICertificatService;


import java.util.List;

@RestController
@RequestMapping("/certificat")
public class CertificatController {

    @Autowired
    ICertificatService certificatService;

    @PostMapping("/addCertificat")
    Certificat addCertificat(@RequestBody Certificat certificat) {
        return certificatService.addCertificat(certificat);
    }
    @PutMapping("/updateCertificat")
    Certificat updateCertificat(@RequestBody Certificat certificat) {
        return certificatService.updateCertificat(certificat);
    }
    @GetMapping("/getAllCertificat")
    List<Certificat> getAllCertificat() {
        return certificatService.getAllCertificat();
    }
    @GetMapping("/getCertificatById/{idcertificat}")
    Certificat getCertificatById(@PathVariable long idcertificat) {
        return certificatService.getCertificatById(idcertificat);
    }
    @DeleteMapping("/deleteCertificat/{idcertificat}")
    Certificat deleteCertificat(@PathVariable long idcertificat) {
        certificatService.deleteCertificat(idcertificat);
        return null;
    }
}
