package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

public interface TaskService {
    Iterable<Task> getAllTasks(Predicate predicate);

    Task getTaskById(Long taskId);

    Task createTask(TaskDto taskDto);

    Task updateTask(Long taskId, TaskDto taskDto);

    void deleteTask(Long taskId);
}
