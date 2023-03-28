package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @Operation(summary = "Application welcome page")
    @ApiResponse(responseCode = "200", description = "Welcome page")
    @GetMapping(path = "/welcome")
    public String root() {
        return "Welcome to Spring";
    }
}
