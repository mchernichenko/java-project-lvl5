package hexlet.code.service.impl;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TokenService;
import hexlet.code.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private static final String INVALID_AUTH_MSG = "Invalid user or password"; // текст ошибки аутентификации
    private static final String AUTH_FIELD = "email"; // в качестве поля авторизации используется email пользователя

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService; // генерилка токенов

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(String email, String password) {
        return this.userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // проверка переданного пароля
                .map(user -> tokenService.expiring(Map.of(AUTH_FIELD, email))) // создание токена по email
                .orElseThrow(() -> new UsernameNotFoundException(INVALID_AUTH_MSG));
    }

    @Override
    public Optional<User> findByToken(String token) {
        return this.userRepository.findByEmail(
                tokenService.verify(token)
                .get(AUTH_FIELD) // из Payload получить email, который был записан при формировании токена
                .toString());
    }
}
