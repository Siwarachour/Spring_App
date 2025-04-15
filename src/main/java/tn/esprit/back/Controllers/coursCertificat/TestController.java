package tn.esprit.back.Controllers.coursCertificat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.coursCertificat.Certificat;
import tn.esprit.back.Entities.coursCertificat.Test;
import tn.esprit.back.Services.coursCertificat.ICertificatService;
import tn.esprit.back.Services.coursCertificat.ITestService;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ITestService testService;

    @PostMapping("/addTest")
    Test addTest(@RequestBody Test test) {
        return testService.addTest(test);
    }
    @PutMapping("/updateTest")
    Test updateTest(@RequestBody Test test) {
        return testService.updateTest(test);
    }
    @GetMapping("/getAllTest")
    List<Test> getAllTest() {
        return testService.getAllTest();
    }
    @GetMapping("/getTestById/{idtest}")
    Test getTestById(@PathVariable long idtest) {
        return testService.getTestById(idtest);
    }
    @DeleteMapping("/deleteTest/{idtest}")
    Test deleteTest(@PathVariable long idtest) {
        testService.deleteTest(idtest);
        return null;
    }
}
