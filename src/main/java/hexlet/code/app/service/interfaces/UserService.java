package hexlet.code.app.service.interfaces;

import hexlet.code.app.dto.UserDtoRequest;
import hexlet.code.app.dto.UserDtoResponse;

import java.util.List;

public interface UserService {

    UserDtoResponse getUserById(long id);
    List<UserDtoResponse> getAllUsers();
    UserDtoResponse createUser(UserDtoRequest userDtoRequest);
    UserDtoResponse updateUser(UserDtoRequest userDtoRequest, long id);
    void deleteUser(long id);

}
