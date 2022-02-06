package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.List;

/*
 * Контроллер реализует следующие end points:
 * GET /api/statuses - получение списка статусов
 * GET /api/statuses/{id} - получение статуса по идентификатору
 * POST /api/statuses - создание нового статуса
 * PUT /api/statuses/{id} - обновление статуса
 * DELETE /api/statuses/{id} - удаление статуса
 */

@Tag(name = "taskStatus", description = "Operations about task status")
@RestController
@RequestMapping("${base-url}" + "/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

    @Operation(summary = "Get all task statuses", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(path = "")
    public List<TaskStatus> getAllStatuses() {
        return taskStatusService.getAllStatus();
    }

    @Operation(summary = "Get task status by id", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(path = "/{id}")
    public TaskStatus getStatus(@PathVariable("id") Long statusId) {
        return taskStatusService.getStatusById(statusId);
    }

    @Operation(summary = "Create task status", security = @SecurityRequirement(name = "Bearer Token"))
    @ApiResponse(responseCode = "201")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public TaskStatus createStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createStatus(taskStatusDto);
    }

    @Operation(summary = "Update task status", security = @SecurityRequirement(name = "Bearer Token"))
    @PutMapping(path = "/{id}")
    public TaskStatus updateStatus(@PathVariable("id") Long statusId, @RequestBody TaskStatusDto taskStatusDto) {
        return taskStatusService.updateStatus(statusId, taskStatusDto);
    }

    @Operation(summary = "Delete task status", security = @SecurityRequirement(name = "Bearer Token"))
    @DeleteMapping(path = "/{id}")
    public void deleteStatus(@PathVariable("id") Long statusId) {
        taskStatusService.deleteStatus(statusId);
    }
}
