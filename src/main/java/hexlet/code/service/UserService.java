package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserByUserId(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    String getCurrentUserName();

    User getCurrentUser();
}
