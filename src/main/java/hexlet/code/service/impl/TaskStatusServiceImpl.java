package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public List<TaskStatus> getAllStatus() {
        return taskStatusRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatus getStatusById(Long statusId) {
        return taskStatusRepository.findById(statusId)
                .orElseThrow(); // NoSuchElementException, если не найден
    }

    @Override
    public TaskStatus createStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateStatus(Long statusId, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatusToUpdate = taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("taskStatusId '%s' not found.", statusId)
                ));
        taskStatusToUpdate.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @Override
    public void deleteStatus(Long statusId) {
        taskStatusRepository.findById(statusId).orElseThrow();
        taskStatusRepository.deleteById(statusId);
    }

/*    private TaskStatusDto toTaskStatusDto(TaskStatus taskStatus) {
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setName(taskStatus.getName());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDto;
    }*/
}
