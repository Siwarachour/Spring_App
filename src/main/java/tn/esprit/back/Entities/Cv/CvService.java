package tn.esprit.back.Entities.Cv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class CvService {
    private final CvRepo cvRepo;

    public Object addCv(Cv cv) {
        return cvRepo.save(cv).getId();
    }
}
