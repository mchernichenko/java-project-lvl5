package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import com.querydsl.core.types.Predicate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/*
 * Контроллер реализует следующие end points:
 * GET /tasks/{id} - получение задачи по идентификатору
 * GET /tasks - получение списка задач
 * POST /tasks - создание новой задачи
 * PUT /tasks/{id} - обновление задачи
 * DELETE /tasks/{id} - удаление задачи
 */

@Tag(name = "task", description = "Operations about task")
@RestController
@RequestMapping("${base-url}" + "/tasks")
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#taskId).get().getAuthor().getEmail() == authentication.getName()
        """;

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Get all tasks", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(path = "")
    public Iterable<Task> getAllTasks(@QuerydslPredicate Predicate predicate) {
        LOGGER.info("GET /tasks --> Predicate: " + predicate.toString());
        return taskService.getAllTasks(predicate);
    }

    @Operation(summary = "Get task by id", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(path = "/{id}")
    public Task getTask(@PathVariable("id") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Operation(summary = "Create task", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponse(responseCode = "201")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public Task createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update test", security = @SecurityRequirement(name = "Bearer Token"))
    @PutMapping(path = "/{id}")
    public Task updateTask(@PathVariable("id") Long taskId, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(taskId, taskDto);
    }

    @Operation(summary = "Delete task", security = @SecurityRequirement(name = "Bearer Token"))
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может удалить только сам себя
    public void deleteTask(@PathVariable("id") Long taskId) {
        taskService.deleteTask(taskId);
    }
}
