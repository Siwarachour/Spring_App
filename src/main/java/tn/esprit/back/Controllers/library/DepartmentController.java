package tn.esprit.back.Controllers.library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.back.Entities.library.Department;
import tn.esprit.back.Services.library.IDepartment;
import java.util.List;


@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    IDepartment departmentService;

    @PutMapping("/update")
    public Department updateDepartment(@RequestBody Department department) {
        return departmentService.updateDepartment(department);
    }
    @PostMapping("/add")
    public Department addDepartment(@RequestBody Department department) {
        return departmentService.addDepartment(department);
    }
    @GetMapping("/getall")
    public List<Department> getAllDepartment() {
        return departmentService.getAllDepartment();
    }
    @GetMapping("/retrieve/{idDepartment}")
    public Department getDepartmentById(@PathVariable long idDepartment) {
        return departmentService.getDepartmentById(idDepartment);
    }
    @DeleteMapping("/delete/{idDepartment}")
    public void deleteDepartment(@PathVariable long idDepartment) {
        departmentService.deleteDepartment(idDepartment);
    }



   /* @PutMapping("/affectUsersToDepartment/{idDepartment}")
    public Department affectUsersToDepartment(@PathVariable Long idDepartment, @RequestBody List<Long> idUsers) {
        return departmentService.assignUsersToDepartment(idDepartment, idUsers);
    }*/
}

