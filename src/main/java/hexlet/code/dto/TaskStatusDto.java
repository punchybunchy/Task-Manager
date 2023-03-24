package hexlet.code.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDto {
    @NotBlank
    private String name;
}
