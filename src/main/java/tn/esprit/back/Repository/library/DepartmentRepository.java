package tn.esprit.back.Repository.library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.back.Entities.library.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
