package hexlet.code.config;

import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }
}
