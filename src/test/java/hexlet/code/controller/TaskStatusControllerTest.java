package hexlet.code.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TokenService;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.util.TestUtils.AUTH_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

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
@DBRider
@DataSet(value = {"users.yml", "taskStatus.yml"}, cleanAfter = true, transactional = true)
//@Transactional
public class TaskStatusControllerTest {
    private static final String BASE_URL = "/api/statuses";
    private static final String LOGIN = "mikhail.chernichenko@gmail.com";
    private String userToken;

    @Autowired
    private TestUtils utils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void before() throws Exception {
        userToken = tokenService.expiring(Map.of(AUTH_FIELD, LOGIN));
    }

    @Test
    void createStatus() throws Exception {
        TaskStatusDto newTaskStatusDto = getTestStatusDto();
        mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(newTaskStatusDto))
                                .contentType(APPLICATION_JSON)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isCreated());

        var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(newTaskStatusDto.getName());
    }

    @Test
    void createStatusFail() throws Exception {
        TaskStatusDto newTaskStatusDto = getTestStatusDto();

        ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(newTaskStatusDto))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTaskStatus() throws Exception {
        final var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("new", "in progress", "on test", "finish");
    }

    @Test
    void getTaskStatusById() throws Exception {

        MockHttpServletRequestBuilder request = get(BASE_URL + "/{id}", 1);

        final var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("new");
        assertThat(response.getContentAsString()).doesNotContain("in progress");
    }

    @Test
    void updateTaskStatus() throws Exception {

        final TaskStatusDto updatedTaskStatusDto = TaskStatusDto.builder()
                .name("updateStatus")
                .build();

        MockHttpServletRequestBuilder updateRequest = put(BASE_URL + "/{id}", 1)
                .content(utils.asJson(updatedTaskStatusDto))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, userToken);

        mockMvc.perform(updateRequest)
                .andExpect(status().isOk());

        TaskStatus actualStatus = taskStatusRepository.findById(1L).get();
        assertThat(actualStatus).isNotNull();
        assertThat(actualStatus.getName()).isEqualTo("updateStatus");
    }

    @Test
    void deleteTaskStatus() throws Exception {
        mockMvc.perform(
                        delete(BASE_URL + "/{id}", 1)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskStatusRepository.existsById(1L)).isFalse();
    }

    private TaskStatusDto getTestStatusDto() {
        return TaskStatusDto.builder()
                .name("testStatusName")
                .build();
    }
}
