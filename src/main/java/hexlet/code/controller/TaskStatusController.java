package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер реализует следующие end points:
 * GET /api/statuses - получение списка статусов
 * GET /api/statuses/{id} - получение статуса по идентификатору
 * POST /api/statuses - создание нового статуса
 * PUT /api/statuses/{id} - обновление статуса
 * DELETE /api/statuses/{id} - удаление статуса
 */

@RestController
@RequestMapping("${base-url}" + "/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping(path = "")
    public List<TaskStatusDto> getAllUsers() {
        return taskStatusService.getAllStatus();
    }

    @GetMapping(path = "/{id}")
    public TaskStatusDto getUser(@PathVariable("id") Long statusId) {
        return taskStatusService.getStatusById(statusId);
    }

    @PostMapping(path = "")
    public TaskStatusDto createUser(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createStatus(taskStatusDto);
    }

    @PutMapping(path = "/{id}")
    public TaskStatusDto updateUser(@PathVariable("id") Long statusId, @RequestBody TaskStatusDto taskStatusDto) {
        return taskStatusService.updateStatus(statusId, taskStatusDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") Long statusId) {
        taskStatusService.deleteStatus(statusId);
    }
}
