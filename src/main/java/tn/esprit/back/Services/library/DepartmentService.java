package tn.esprit.back.Services.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.library.Department;
import tn.esprit.back.Repository.library.DepartmentRepository;
import java.util.List;
import java.util.Optional;



@Service
public class DepartmentService implements IDepartment {

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public Department addDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(long idDepartment) {
        departmentRepository.deleteById(idDepartment);
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(long idDepartment) {
        Optional<Department> department = departmentRepository.findById(idDepartment);
        return department.orElse(null);
    }
}
