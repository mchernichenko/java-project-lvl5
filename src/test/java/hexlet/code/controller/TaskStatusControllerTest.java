package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TokenService;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Контроллер реализует следующие end points:
 * GET /api/statuses - получение списка статусов
 * GET /api/statuses/{id} - получение статуса по идентификатору
 * POST /api/statuses - создание нового статуса
 * PUT /api/statuses/{id} - обновление статуса
 * DELETE /api/statuses/{id} - удаление статуса
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskStatusControllerTest {
    private static final String BASE_URL = "/api/statuses";
    private String userToken;

    @Autowired
    private TestUtils utils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void before() throws Exception {
        final UserDto userDto = utils.getTestValidUserDto();

        mockMvc.perform(
                post("/api/users")
                        .content(utils.asJson(userDto))
                        .contentType(APPLICATION_JSON));

        userToken = tokenService.expiring(Map.of(AUTH_FIELD, userDto.getEmail()));
    }

    @AfterEach
    public void clear() {
        taskStatusRepository.deleteAll();
    }

    @Test
    void createStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());

        mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(utils.getTestStatusDto()))
                                .contentType(APPLICATION_JSON)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk());

        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    void createStatusFail() throws Exception {
        assertEquals(0, taskStatusRepository.count());

        ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(utils.getTestStatusDto()))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTaskStatus() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .content(utils.asJson(utils.getTestStatusDto()))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, userToken));

        final var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = utils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    void getTaskStatusById() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .content(utils.asJson(utils.getTestStatusDto()))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, userToken));

        final TaskStatus expectedTackStatus = taskStatusRepository.findAll().get(0);

        MockHttpServletRequestBuilder request = get(BASE_URL + "/{id}", expectedTackStatus.getId());

        final var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = utils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedTackStatus.getId(), taskStatus.getId());
        assertEquals(expectedTackStatus.getName(), taskStatus.getName());
    }

    @Test
    void updateTaskStatus() throws Exception {
        final TaskStatusDto taskStatusDto = utils.getTestStatusDto();

        mockMvc.perform(
                post(BASE_URL)
                        .content(utils.asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, userToken)
        );
        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        final TaskStatusDto updatedTaskStatusDto = new TaskStatusDto(null, "updateStatus", null);

        MockHttpServletRequestBuilder updateRequest = put(BASE_URL + "/{id}", taskStatus.getId())
                .content(utils.asJson(updatedTaskStatusDto))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, userToken);

        mockMvc.perform(updateRequest)
                .andExpect(status().isOk());

        assertTrue(taskStatusRepository.existsById(taskStatus.getId()));
        assertNotNull(taskStatusRepository.findByName(updatedTaskStatusDto.getName()).orElse(null));
        assertNull(taskStatusRepository.findByName(taskStatusDto.getName()).orElse(null));
    }

    @Test
    void deleteTaskStatus() throws Exception {
        final TaskStatusDto taskStatusDto = utils.getTestStatusDto();

        mockMvc.perform(
                post(BASE_URL)
                        .content(utils.asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, userToken)
        );

        final Long statusId = taskStatusRepository.findByName(taskStatusDto.getName()).get().getId();

        MockHttpServletRequestBuilder request = delete(BASE_URL + "/{id}", statusId)
                .header(AUTHORIZATION, userToken);

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }
}
