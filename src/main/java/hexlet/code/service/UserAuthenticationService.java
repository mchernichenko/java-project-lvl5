package hexlet.code.service;

import hexlet.code.model.User;

import java.util.Optional;

public interface UserAuthenticationService {
    String login(String email, String password);

    Optional<User> findByToken(String token);
}
