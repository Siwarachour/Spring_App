package tn.esprit.back.Services.User;

import tn.esprit.back.Entities.User.User;

public interface userService {
    User getUser(Long id);
    User getUserByEmail(String email);
    User getUserByUsername(String username);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

}
