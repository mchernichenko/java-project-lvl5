package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.service.UserAuthenticationService;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер реализует следующие end points:
 * GET /api/users/{id}  - получение пользователя по идентификатору
 * GET /api/users - получение списка пользователей
 * POST /api/users - создание пользователя
 * PUT /api/users/{id} - обновление пользователя
 * DELETE /api/users/{id} - удаление пользователя
 */

@Tag(name = "user", description = "Operations about user")
@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#userId).get().getEmail() == authentication.getName()
        """;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Get list of all users")
    @GetMapping(path = "")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by id", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user information"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может запрашивать только сам себя
    public User getUser(@PathVariable("id") Long userId) {
        return userService.getUserByUserId(userId);
    }

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "422", description = "User invalid/Invalid input")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public User createUser(@RequestBody @Valid UserDto userDto) {
        //userAuthenticationService.login(userDto.getEmail(), userDto.getPassword()); // возвращаем токен
        return userService.createUser(userDto);
    }

    @Operation(summary = "Update user", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "422", description = "Invalid input")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может редактировать только сам себя
    public User updateUser(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @Operation(summary = "Delete user", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может удалить только сам себя
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
