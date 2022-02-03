package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    Task getTaskById(Long taskId);

    Task createTask(TaskDto taskDto);

    Task updateTask(Long taskId, TaskDto taskDto);

    void deleteTask(Long taskId);
}
