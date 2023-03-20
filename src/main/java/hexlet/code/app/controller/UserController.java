package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;
import hexlet.code.app.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final String AUTHORIZED_USERS_ONLY = """
        @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    public static final String ID = "/{id}";
    private final UserService userService;

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ResponseStatus(CREATED)
    @PostMapping(path = "")
    public User registerNewUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(path = ID)
    public User getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    // Content используется для указания содержимого ответа
    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = User.class))
    ))
    @GetMapping(path = "")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PutMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public User updateUser(
            @RequestBody @Valid UserDto userDto,
            @PathVariable long id) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}