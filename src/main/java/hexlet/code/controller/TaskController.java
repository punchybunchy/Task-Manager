package hexlet.code.controller;


import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.interfaces.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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

    @Operation(summary = "Get a task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @GetMapping(path = ID)
    public Task getTask(
            @Parameter(description = "id of a task to be searched")
            @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tasks",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @GetMapping(path = "")
    public List<Task> getTasks(
            @Parameter(hidden = true) @QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskService.getAllTasks(predicate);
    }

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =  "Task is created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @ResponseStatus(CREATED)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PostMapping(path = "")
    public Task registerNewTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PutMapping(path = ID)
    public Task updateTask(
            @RequestBody @Valid TaskDto taskDto,
            @Parameter(description = "id of a task to be updated")
            @PathVariable Long id) {
        return taskService.updateTask(taskDto, id);
    }

    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is deleted", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @PreAuthorize(TASK_AUTHOR_ONLY)
    @DeleteMapping(path = ID)
    public void deleteTask(
            @Parameter(description = "id of a task to be deleted")
            @PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
