package tn.esprit.back.Repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.Role.Role;

public interface roleRepository extends JpaRepository<Role, Integer> {
}
