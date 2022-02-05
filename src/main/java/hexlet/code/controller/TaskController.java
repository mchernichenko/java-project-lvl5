package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import com.querydsl.core.types.Predicate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Контроллер реализует следующие end points:
 * GET /tasks/{id} - получение задачи по идентификатору
 * GET /tasks - получение списка задач
 * POST /tasks - создание новой задачи
 * PUT /tasks/{id} - обновление задачи
 * DELETE /tasks/{id} - удаление задачи
 */

@RestController
@RequestMapping("${base-url}" + "/tasks")
public class TaskController {

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#taskId).get().getAuthor().getEmail() == authentication.getName()
        """;

    @Autowired
    private TaskService taskService;

    @GetMapping(path = "")
    public Iterable<Task> getAllTasks(@QuerydslPredicate Predicate predicate) {
        return taskService.getAllTasks(predicate);
    }

    @GetMapping(path = "/{id}")
    public Task getTask(@PathVariable("id") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @PostMapping(path = "")
    public Task createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping(path = "/{id}")
    public Task updateTask(@PathVariable("id") Long taskId, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(taskId, taskDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID) // пользователь может удалить только сам себя
    public void deleteTask(@PathVariable("id") Long taskId) {
        taskService.deleteTask(taskId);
    }
}
