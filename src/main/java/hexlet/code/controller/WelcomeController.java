package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;

@RestController
public class WelcomeController {
    @Value("${rollbar_token}")
    private String rollbarToken;

    private Rollbar rollbar = Rollbar.init(
            withAccessToken(rollbarToken)
            .environment("development")
            .build());

    void sendRollbarDebug() {
        System.out.println("TOKEN HERE: " + rollbarToken); //тут смотрю что токен точно не ноль
        rollbar.debug("New check message to send");
    }

    @Operation(summary = "Application welcome page")
    @ApiResponse(responseCode = "200", description = "Welcome page")
    @GetMapping(path = "/welcome")
    public String root() {
        sendRollbarDebug();
        return "Welcome to Spring";
    }
}
