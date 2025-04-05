package tn.esprit.back.Repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.User.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByusername(String username);
    User findByEmail(String email);
    List<String> findRolesByUsername(String username);
    Optional<User> findByResetToken(String resetToken);

}
