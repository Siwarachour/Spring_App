package tn.esprit.back.Services.DiplomaMangement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.University.University;
import tn.esprit.back.Repository.diploma.UniversityRepository;

import java.util.List;

@Service
public class UniversityService implements ICrudService<University,Long> {
    
    @Autowired
    private UniversityRepository repo;
    
    @Override
    public University add(University data) {
        return repo.save(data);
    }

    @Override
    public University update(University data) {
        return repo.save(data);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<University> getAll() {
        return repo.findAll();
    }

    @Override
    public University get(Long id) {
        return repo.findById(id).get();
    }
}
