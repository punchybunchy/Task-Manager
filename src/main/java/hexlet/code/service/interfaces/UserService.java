package hexlet.code.service.interfaces;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {

    User getUserById(long id);
    List<User> getAllUsers();
    User createUser(UserDto userDto);
    User updateUser(UserDto userDto, long id);
    void deleteUser(long id);

    User getCurrentUser();

}
