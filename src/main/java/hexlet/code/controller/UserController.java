package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.service.UserAuthenticationService;
import hexlet.code.service.UserService;
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

@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#userId).get().getEmail() == authentication.getName()
        """;

    @Autowired
    private UserService userService;

/*    @Autowired
    private UserRepository userRepository;*/

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping(path = "")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может запрашивать только сам себя
    public UserDto getUser(@PathVariable("id") Long userId) {
        return userService.getUserByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public String createUser(@RequestBody @Valid UserDto userDto) {
        userService.createUser(userDto);
        return userAuthenticationService.login(userDto.getEmail(), userDto.getPassword()); // возвращаем токен
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может редактировать только сам себя
    public UserDto updateUser(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может удалить только сам себя
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
