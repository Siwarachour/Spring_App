package tn.esprit.back.Repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.User.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByusername(String username);
    User findByEmail(String email);
    List<String> findRolesByUsername(String username);
}
