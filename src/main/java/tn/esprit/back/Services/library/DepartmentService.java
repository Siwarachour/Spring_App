package tn.esprit.back.Services.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.library.Department;
import tn.esprit.back.Repository.User.UserRepository;
import tn.esprit.back.Repository.library.DepartmentRepository;
import java.util.List;
import java.util.Optional;



@Service
public class DepartmentService implements IDepartment {

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    UserRepository userRepository;

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




    /*@Override
    public Department assignUsersToDepartment(Long departmentId, List<Long> id) {
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        List<User> users = userRepository.findAllById(id);
        users.forEach(user -> user.setDepartment(department));
        userRepository.saveAll(users);
        return department;
    }*/
}
