package hexlet.code.controller;


import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.interfaces.TaskService;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";
    private static final String TASK_AUTHOR_ONLY = """
        @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private static final String AUTHORIZED_USERS_ONLY = "isAuthenticated()";

    private final TaskService taskService;

    @Operation(summary = "Get task by id")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(path = ID)
    public Task getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Task.class))))
    @GetMapping(path = "")
    public List<Task> getTasks() {
        return taskService.getAllTasks();
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task has created")
    @ResponseStatus(CREATED)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PostMapping(path = "")
    public Task registerNewTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update task")
    @ApiResponse(responseCode = "200", description = "Task has been updated")
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PutMapping(path = ID)
    public Task updateTask(@RequestBody @Valid TaskDto taskDto, @PathVariable Long id) {
        return taskService.updateTask(taskDto, id);
    }

    @Operation(summary = "Delete task")
    @ApiResponse(responseCode = "200", description = "Task has been deleted")
    @PreAuthorize(TASK_AUTHOR_ONLY)
    @DeleteMapping(path = ID)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
