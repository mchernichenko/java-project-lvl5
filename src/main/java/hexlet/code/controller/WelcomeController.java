package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "welcome", description = "Welcome page")
@RestController
public class WelcomeController {
    @Operation(summary = "Welcome")
    @GetMapping("/welcome")
    public String root() {
        return "Welcome to Spring";
    }
}
