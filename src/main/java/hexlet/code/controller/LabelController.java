package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.interfaces.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";
    private static final String AUTHORIZED_USERS_ONLY = "isAuthenticated()";

    private final LabelService labelService;

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the label",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class)) }),
            @ApiResponse(responseCode = "404", description = "Label not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @GetMapping(path = ID)
    public Label getLabel(
            @Parameter(description = "id of a label to be searched")
            @PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the labels",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @GetMapping(path = "")
    public List<Label> getLabels() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label is created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @ResponseStatus(CREATED)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PostMapping(path = "")
    public Label regLabel(@RequestBody LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label is updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class)) }),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Label not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PutMapping(path = ID)
    public Label updateLabel(
            @RequestBody LabelDto labelDto,
            @Parameter(description = "id of a label to be updated")
            @PathVariable Long id) {
        return labelService.updateLabel(labelDto, id);
    }

    @Operation(summary = "Delete label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label is deleted", content = @Content),
            @ApiResponse(responseCode = "422", description = "Request contains invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Label not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content)
    })
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @DeleteMapping(path = ID)
    public void deleteLabel(
            @Parameter(description = "id of a label to be deleted")
            @PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
