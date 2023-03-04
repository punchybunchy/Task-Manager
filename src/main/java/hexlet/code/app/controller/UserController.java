package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDtoRequest;
import hexlet.code.app.dto.UserDtoResponse;
import hexlet.code.app.exceptionsHandler.UserNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hexlet.code.app.controller.UserController.USER_CONTROLLER_PATH;


@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping(path = ID)
    public UserDtoResponse getUser(@PathVariable long id) {
        return userServiceImpl.getUserById(id);
    }

//    @GetMapping(path = "/{id}")
//    public User getUser(@PathVariable long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException("There is no user"));
//    }

    @GetMapping(path = "")
    public List<UserDtoResponse> getAll() {
        return userServiceImpl.getAllUsers();
    }

    @PostMapping(path = "")
    public UserDtoResponse createUser(@RequestBody UserDtoRequest userDtoRequest) {
        return userServiceImpl.createUser(userDtoRequest);
    }

    @PutMapping(path = ID)
    public UserDtoResponse updateUser(
            @RequestBody UserDtoRequest userDtoRequest,
            @PathVariable long id) {
        return userServiceImpl.updateUser(userDtoRequest, id);
    }

    @DeleteMapping(path = ID)
    public void deleteUser(@PathVariable long id) {
        userServiceImpl.deleteUser(id);
    }
}