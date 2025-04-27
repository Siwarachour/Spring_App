package tn.esprit.back.Controllers.diploma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.StudyPlan.StudyPlan;
import tn.esprit.back.Services.DiplomaMangement.StudyPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/study-plan")
public class StudyPlanController {
    @Autowired
    private StudyPlanService service;

    @PostMapping("/add")
    public StudyPlan add(@RequestBody StudyPlan val) {
        return service.add(val);
    }

    @PutMapping("/update")
    public StudyPlan update(@RequestBody StudyPlan val) {
        return service.update(val);
    }

    @GetMapping("/getAll")
    public List<StudyPlan> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public StudyPlan getById(@PathVariable long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
