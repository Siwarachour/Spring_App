package tn.esprit.back.Controllers.diploma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.University.University;
import tn.esprit.back.Services.DiplomaMangement.UniversityService;

import java.util.List;

@RestController
@RequestMapping("/api/university")
public class UniversityController {
    @Autowired
    private UniversityService service;

    @PostMapping("/add")
    public University add(@RequestBody tn.esprit.back.Entities.University.University val) { return service.add(val); }

    @PutMapping("/update")
    public University update(@RequestBody University val) { return service.update(val); }

    @GetMapping("/getAll")
    public List<University> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public University getById(@PathVariable long id) { return service.get(id); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
