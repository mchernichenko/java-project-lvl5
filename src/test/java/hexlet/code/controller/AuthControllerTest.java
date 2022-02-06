package hexlet.code.controller;

import hexlet.code.dto.LoginDto;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.util.TestUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Контроллер реализует следующие end points:
 * GET /api/login  - аутентификация пользователя
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    private static final String BASE_URL = "/api/login";

    @Autowired
    private TestUtils utils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        mockMvc.perform(
                post("/api/users")
                        .content(asJson(utils.getTestValidUserDto()))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated());

        final LoginDto loginDto = new LoginDto(
                utils.getTestValidUserDto().getEmail(),
                utils.getTestValidUserDto().getPassword()
        );
        final var loginRequest = post(BASE_URL)
                .content(asJson(loginDto))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(loginRequest)
                .andExpect(status().isOk());
    }

    @Test
    void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                utils.getTestValidUserDto().getEmail(),
                utils.getTestValidUserDto().getPassword()
        );

        final var loginRequest = post(BASE_URL)
                .content(asJson(loginDto))
                .contentType(APPLICATION_JSON);

        mockMvc.perform(loginRequest)
                .andExpect(status().isUnauthorized());
    }
}
