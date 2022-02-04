package hexlet.code.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TokenService;
import hexlet.code.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

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
 * GET /tasks/{id} - получение задачи по идентификатору
 * GET /tasks - получение списка задач
 * POST /tasks - создание новой задачи
 * PUT /tasks/{id} - обновление задачи
 * DELETE /tasks/{id} - удаление задачи
 */

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@DataSet(value = {"users.yml", "taskStatus.yml", "labels.yml", "task.yml"}, cleanAfter = true, transactional = true)
public class TaskControllerTest {
    private static final String BASE_URL = "/api/tasks";
    private static final String LOGIN = "mikhail.chernichenko@gmail.com";

    private String userToken;;

    @Autowired
    private TestUtils utils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TokenService tokenService;

    @BeforeEach
    public void before() throws Exception {
        userToken = tokenService.expiring(Map.of(AUTH_FIELD, LOGIN));
    }

    @Test
    void createTask() throws Exception {
        TaskDto newTaskDto = getTestTaskDto();

        mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(newTaskDto))
                                .contentType(APPLICATION_JSON)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk());

        var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(newTaskDto.getName());
        assertThat(response.getContentAsString()).contains("label_1", "label_2");
    }

    @Test
    void createTaskFail() throws Exception {

        TaskDto taskDto = getTestTaskDto();

        mockMvc.perform(
                        post(BASE_URL)
                                .content(utils.asJson(taskDto))
                                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTasks() throws Exception {

        final var response = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Task1", "Task2", "Task3");
    }

    @Test
    void getTaskById() throws Exception {
        final var response = mockMvc.perform(get(BASE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task1", "Description1", "label_1", "label_2");
    }

    @Test
    void updateTask() throws Exception {
        final TaskDto updateTaskDto = TaskDto.builder()
                .name("update_name")
                .description("update_description")
                .executorId(2L)
                .taskStatusId(2L)
                .labelIds(Set.of(2L))
                .build();

        mockMvc.perform(
                        put(BASE_URL + "/{id}", 2)
                                .content(utils.asJson(updateTaskDto))
                                .contentType(APPLICATION_JSON)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk());

        Task actualTask = taskRepository.findById(2L).get();
        assertThat(actualTask).isNotNull();
        assertThat(actualTask.getName()).isEqualTo("update_name");
    }

    @Test
    void deleteTask() throws Exception {
        mockMvc.perform(
                        delete(BASE_URL + "/{id}", 1)
                                .header(AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(taskRepository.existsById(1L)).isFalse();
    }

    private TaskDto getTestTaskDto() {
        return TaskDto.builder()
                .name("New task")
                .description("Description new task")
                .executorId(1L)
                .taskStatusId(1L)
                .labelIds(Set.of(1L, 2L))
                .build();
    }
}
