package hexlet.code.app.service.interfaces;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;

import java.util.List;

public interface UserService {

    User getUserById(long id);
    List<User> getAllUsers();
    User createUser(UserDto userDto);
    User updateUser(UserDto userDto, long id);
    void deleteUser(long id);

}
