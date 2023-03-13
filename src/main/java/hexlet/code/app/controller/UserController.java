package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDtoRequest;
import hexlet.code.app.dto.UserDtoResponse;
import hexlet.code.app.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.app.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private final UserService userService;

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ResponseStatus(CREATED)
    @PostMapping(path = "")
    public UserDtoResponse registerNewUser(@RequestBody @Valid UserDtoRequest userDtoRequest) {
        return userService.createUser(userDtoRequest);
    }

    @GetMapping(path = ID)
    public UserDtoResponse getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping(path = "")
    public List<UserDtoResponse> getAll() {
        return userService.getAllUsers();
    }

    @PutMapping(path = ID)
    public UserDtoResponse updateUser(
            @RequestBody @Valid UserDtoRequest userDtoRequest,
            @PathVariable long id) {
        return userService.updateUser(userDtoRequest, id);
    }

    @DeleteMapping(path = ID)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}