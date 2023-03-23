package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


//Add Lombok @Data annotation which contains a shortcut for @ToString, @EqualsAndHashCode, @Getter on all fields,
// and @Setter on all non-final fields, and @RequiredArgsConstructor!
@Getter
@Setter
public class UserDto {

//    private long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;

    public UserDto(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
}
