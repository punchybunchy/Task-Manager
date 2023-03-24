package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.service.interfaces.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(path = ID)
    public Label getLabel(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get all labels")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class))))
    @GetMapping(path = "")
    public List<Label> getLabels() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "Label has been created")
    @ResponseStatus(CREATED)
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PostMapping(path = "")
    public Label regLabel(@RequestBody LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label")
    @ApiResponse(responseCode = "200", description = "Label has been updated")
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @PutMapping(path = ID)
    public Label updateLabel(@RequestBody LabelDto labelDto, @PathVariable Long id) {
        return labelService.updateLabel(labelDto, id);
    }

    @Operation(summary = "Delete label")
    @ApiResponse(responseCode = "200", description = "Label has been deleted")
    @PreAuthorize(AUTHORIZED_USERS_ONLY)
    @DeleteMapping(path = ID)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
