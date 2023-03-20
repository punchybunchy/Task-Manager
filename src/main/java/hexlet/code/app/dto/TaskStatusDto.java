package hexlet.code.app.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDto {

    private Long id;

    @NotBlank
    private String name;

    private Date createdAt;

}
