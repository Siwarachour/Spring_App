package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;
    @PostMapping   ("/add")
    public ResponseEntity <Integer> addCv(@RequestBody Cv cv, Authentication connecteduser) {
        return ResponseEntity.ok((Integer) cvService.addCv(cv,connecteduser));
    }



}