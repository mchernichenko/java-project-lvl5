package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TokenService;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static hexlet.code.util.TestUtils.AUTH_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Контроллер реализует следующие end points:
 * GET /api/users - получение списка пользователей
 * GET /api/users/{id}  - получение пользователя по идентификатору
 * POST /api/users - создание пользователя
 * PUT /api/users/{id} - обновление пользователя
 * DELETE /api/users/{id} - удаление пользователя
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@DBRider
//@DataSet("user.yml")
public class UserControllerTest {
    private static final String BASE_URL = "/api/users";

    @Autowired
    private TestUtils utils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() throws Exception {
        assertEquals(0, userRepository.count());

        ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(utils.getTestValidUserDto()))
                                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, userRepository.count());
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .content(utils.asJson(utils.getTestValidUserDto()))
                .contentType(APPLICATION_JSON));

        final User expectedUser = userRepository.findAll().get(0);
        final String token = tokenService.expiring(Map.of(AUTH_FIELD, expectedUser.getEmail()));

        MockHttpServletRequestBuilder request = get(BASE_URL + "/{id}", expectedUser.getId())
                .header(AUTHORIZATION, token);

        final var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = utils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .content(utils.asJson(utils.getTestValidUserDto()))
                .contentType(APPLICATION_JSON));

        final var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = utils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(users).hasSize(1);
    }

    @Test
    void updateUser() throws Exception {
        final UserDto userDto = utils.getTestValidUserDto();

        mockMvc.perform(post(BASE_URL)
                .content(utils.asJson(userDto))
                .contentType(APPLICATION_JSON));
        final User testUser = userRepository.findAll().get(0);

        final UserDto updatedUserDto = UserDto.builder()
                .firstName("first_upd")
                .lastName("last_upd")
                .email("email_upd@mail.ru")
                .password("pwd_upd")
                .build();

        final String token = tokenService.expiring(Map.of(AUTH_FIELD, userDto.getEmail()));
        MockHttpServletRequestBuilder updateRequest = put(BASE_URL + "/{id}", testUser.getId())
                .content(utils.asJson(updatedUserDto))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, token);

        mockMvc.perform(updateRequest)
                .andExpect(status().isOk());

        assertTrue(userRepository.existsById(testUser.getId()));
        assertNotNull(userRepository.findByEmail(updatedUserDto.getEmail()).orElse(null));
        assertNull(userRepository.findByEmail(userDto.getEmail()).orElse(null));
    }

    @Test
    void deleteUser() throws Exception {
        final UserDto userDto = utils.getTestValidUserDto();

        mockMvc.perform(
                post(BASE_URL)
                        .content(utils.asJson(userDto))
                        .contentType(APPLICATION_JSON));

        final Long userId = userRepository.findByEmail(userDto.getEmail()).get().getId();

        final String token = tokenService.expiring(Map.of(AUTH_FIELD, userDto.getEmail()));
        MockHttpServletRequestBuilder request = delete(BASE_URL + "/{id}", userId);
        request.header(AUTHORIZATION, token);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }
}
