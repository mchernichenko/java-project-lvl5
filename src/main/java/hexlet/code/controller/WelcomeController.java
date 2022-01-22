package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @SuppressWarnings("checkstyle:DesignForExtension")
    @GetMapping("/welcome")
    public String root() {
        return "Welcome to Spring";
    }
}
