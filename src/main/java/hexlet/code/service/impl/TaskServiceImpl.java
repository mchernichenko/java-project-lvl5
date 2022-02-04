package hexlet.code.service.impl;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserService userService;
    private final LabelRepository labelRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        User author = userService.getCurrentUser();
        TaskStatus taskStatus = this.taskStatusRepository.findById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("taskStatusId '%s' not found.", taskDto.getTaskStatusId())
                ));
        User executor = userRepository.findById(taskDto.getExecutorId()).orElse(null);

        Set<Label> labelSet = taskDto.getLabelIds().stream()
                .map(x -> labelRepository.findById(x).orElseThrow())
                .collect(Collectors.toSet());
        Task task = Task.builder()
                .author(author)
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .taskStatus(taskStatus)
                .executor(executor)
                .labels(labelSet)
                .build();

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long taskId, TaskDto taskDto) {
        Task taskToUpdate = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("taskId '%s' not found.", taskId)
                ));
        TaskStatus updateTaskStatus = this.taskStatusRepository.findById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("taskStatusId '%s' not found.", taskDto.getTaskStatusId())
                ));
        Set<Label> updateLabelSet = taskDto.getLabelIds().stream()
                .map(x -> labelRepository.findById(x).get())
                .collect(Collectors.toSet());

        User udtateExecutor = userRepository.findById(taskDto.getExecutorId()).orElse(null);
        taskToUpdate.setName(taskDto.getName());
        taskToUpdate.setDescription(taskDto.getDescription());
        taskToUpdate.setTaskStatus(updateTaskStatus);
        taskToUpdate.setExecutor(udtateExecutor);
        taskToUpdate.setLabels(updateLabelSet);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    public void deleteTask(Long taskId) {
      //  taskRepository.findById(taskId).orElseThrow();
        taskRepository.deleteById(taskId);
    }
}
