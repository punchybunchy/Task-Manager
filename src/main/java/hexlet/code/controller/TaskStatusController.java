package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.interfaces.TaskStatusService;
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

import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private static final String AUTHORIZED_USERS_ONLY = "isAuthenticated()";

    private final TaskStatusService taskStatusService;


    @Operation(summary = "Create new status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @ResponseStatus(CREATED)
    @PostMapping(path = "")
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createStatus(taskStatusDto);
    }

    @Operation(summary = "Find status by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation")
    })
    @GetMapping(path = ID)
    public TaskStatus findTaskStatus(@PathVariable Long id) {
        return taskStatusService.getStatus(id);
    }

    @Operation(summary = "Find all statuses")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Statuses found",
            content = @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping(path = "")
    public List<TaskStatus> findAllTaskStatuses() {
        return taskStatusService.getStatuses();
    }

    @Operation(summary = "Update status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @PutMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public TaskStatus updateTaskStatus(
            @RequestBody @Valid TaskStatusDto taskStatusDto,
            @PathVariable Long id) {
        return taskStatusService.updateStatus(taskStatusDto, id);
    }

    @Operation(summary = "Delete status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @DeleteMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteStatus(id);
    }


}
