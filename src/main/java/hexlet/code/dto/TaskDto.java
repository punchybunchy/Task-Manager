package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Long authorId;
    private Long executorId;
    @NotNull
    private Long taskStatusId;
}
