package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.interfaces.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private static final String AUTHORIZED_USERS_ONLY = "isAuthenticated()";

    private final TaskStatusService taskStatusService;


    @Operation(summary = "Create new status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status is created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content)
    })
    @ResponseStatus(CREATED)
    @PostMapping(path = "")
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createStatus(taskStatusDto);
    }

    @Operation(summary = "Find status by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the status",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class)) }),
            @ApiResponse(responseCode = "404", description = "Status not found", content = @Content)
    })
    @GetMapping(path = ID)
    public TaskStatus findTaskStatus(
            @Parameter(description = "id of task status to be searched")
            @PathVariable Long id) {
        return taskStatusService.getStatus(id);
    }

    @Operation(summary = "Find all statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the statuses",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class)) }),
    })
    @GetMapping(path = "")
    public List<TaskStatus> findAllTaskStatuses() {
        return taskStatusService.getStatuses();
    }

    @Operation(summary = "Update status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status is updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Status not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PutMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public TaskStatus updateTaskStatus(
            @RequestBody @Valid TaskStatusDto taskStatusDto,
            @Parameter(description = "id of task status to be updated")
            @PathVariable Long id) {
        return taskStatusService.updateStatus(taskStatusDto, id);
    }

    @Operation(summary = "Delete status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status is deleted", content = @Content),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Status not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @DeleteMapping(path = ID)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    public void deleteTaskStatus(
            @Parameter(description = "id of task status to be deleted")
            @PathVariable Long id) {
        taskStatusService.deleteStatus(id);
    }


}
