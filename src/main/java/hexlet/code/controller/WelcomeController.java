package hexlet.code.controller;

//import com.rollbar.notifier.Rollbar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WelcomeController {

//    @Autowired
//    private Rollbar rollbar;

    @Operation(summary = "Application welcome page")
    @ApiResponse(responseCode = "200", description = "Welcome page")
    @GetMapping(path = "/welcome")
    public String root() {
//        rollbar.debug("Here is sample debug message");
        return "Welcome to Spring";
    }
}
