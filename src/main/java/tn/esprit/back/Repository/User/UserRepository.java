package tn.esprit.back.Repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.back.Entities.User.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByusername(String username);
}
