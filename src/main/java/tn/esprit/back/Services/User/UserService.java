package tn.esprit.back.Services.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Repository.User.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(int id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setBirthday(updatedUser.getBirthday());
            user.setAddress(updatedUser.getAddress());
            user.setPhone(updatedUser.getPhone());
            user.setEmail(updatedUser.getEmail());
            user.setDescription(updatedUser.getDescription());
            return userRepository.save(user);
        }).orElseThrow();
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public User toggleApproval(int id) {
        return userRepository.findById(id).map(user -> {
            user.setApprouve(!user.isApprouve());
            return userRepository.save(user);
        }).orElseThrow();
    }
}

