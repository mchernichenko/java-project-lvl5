package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(path = "")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    public UserDto getUser(@PathVariable("id") Long userId) {
        return userService.getUserByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping(path = "/{id}")
    public UserDto updateUser(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
