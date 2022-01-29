package hexlet.code.controller;

import hexlet.code.dto.LoginDto;
import hexlet.code.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Контроллер реализует следующие end points:
 * GET /api/login  - получение токена JWT по логину(email) и паролю
 */

@RestController
@RequestMapping("${base-url}" + "/login")
public class AuthController {

    @Autowired
    private UserAuthenticationService authenticationService;

    @PostMapping(path = "")
    public String login(@RequestBody @Valid final LoginDto loginDto) {
        return authenticationService.login(loginDto.getEmail(), loginDto.getPassword());
    }
}