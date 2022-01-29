package hexlet.code.service.impl;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(this::toUserDto)
                .orElseThrow(); // NoSuchElementException, если не найден
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
      //  user.setPassword(userDto.getPassword());
        userRepository.save(user);
        userDto.setId(user.getId());
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userToUpdate = userRepository.findById(userId).orElseThrow(); // NoSuchElementException, если не найден
        userToUpdate.setFirstName(userDto.getFirstName());
        userToUpdate.setLastName(userDto.getLastName());
        userToUpdate.setEmail(userDto.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
     //   userToUpdate.setPassword(userDto.getPassword());
        userRepository.save(userToUpdate);
        userDto.setId(userToUpdate.getId());
        userDto.setCreatedAt(userToUpdate.getCreatedAt());
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }
}
