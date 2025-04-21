package tn.esprit.back.Entities.Application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.Cv.Cv;
import tn.esprit.back.Entities.Cv.CvService;
import tn.esprit.back.Entities.User.User;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final Applicationservice applicationservice;
    @PostMapping("/add")
    public ResponseEntity<Integer> addapplication(@RequestBody Application application, Authentication connecteduser) {
        return ResponseEntity.ok((Integer) applicationservice.addApplication(application,connecteduser));
    }

    @GetMapping("/getall")
    public List<Application> getApplications() {return  applicationservice.getApplications();}


    @GetMapping("/accepted-users")
    public ResponseEntity<List<User>> getAcceptedUsers() {
        return ResponseEntity.ok(applicationservice.getUsersWithAcceptedApplications());
    }

    @PutMapping("/update-interview-status/{id}")
    public ResponseEntity<Application> updateInterviewStatus(@PathVariable Integer id, @RequestParam InterviewStatus status) {
        return ResponseEntity.ok(applicationservice.updateInterviewStatus(id, status));
    }






}
