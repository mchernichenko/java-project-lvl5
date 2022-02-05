package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @Operation(summary = "Welcome")
    @GetMapping("/welcome")
    public String root() {
        return "Welcome to Spring";
    }
}
