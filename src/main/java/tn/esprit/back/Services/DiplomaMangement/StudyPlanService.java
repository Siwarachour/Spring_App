package tn.esprit.back.Services.DiplomaMangement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.StudyPlan.StudyPlan;
import tn.esprit.back.Repository.diploma.StudyPlanRepository;

import java.util.List;

@Service
public class StudyPlanService implements ICrudService<StudyPlan,Long> {
    @Autowired
    private StudyPlanRepository repo;
    @Override
    public StudyPlan add(StudyPlan data) {
        return repo.save(data);
    }

    @Override
    public StudyPlan update(StudyPlan data) {
        return repo.save(data);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<StudyPlan> getAll() {
        return repo.findAll();
    }

    @Override
    public StudyPlan get(Long id) {
        return repo.findById(id).get();
    }
}
