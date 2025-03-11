package tn.esprit.back.Services.library;
import tn.esprit.back.Entities.library.Department;
import java.util.List;

public interface IDepartment {

    Department addDepartment(Department department);
    Department updateDepartment(Department department);
    void deleteDepartment(long idDepartment );
    List<Department> getAllDepartment();
    Department getDepartmentById(long idDepartment);


}
