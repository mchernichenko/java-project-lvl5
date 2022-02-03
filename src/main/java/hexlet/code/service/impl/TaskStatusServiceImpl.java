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
    public List<TaskStatusDto> getAllStatus() {
        return taskStatusRepository.findAll().stream()
                .map(this::toTaskStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatusDto getStatusById(Long statusId) {
        return taskStatusRepository.findById(statusId)
                .map(this::toTaskStatusDto)
                .orElseThrow(); // NoSuchElementException, если не найден
    }

    @Override
    public TaskStatusDto createStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        taskStatusRepository.save(taskStatus);
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDto;
    }

    @Override
    public TaskStatusDto updateStatus(Long statusId, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatusToUpdate = taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("taskStatusId '%s' not found.", statusId)
                ));
        taskStatusToUpdate.setName(taskStatusDto.getName());
        taskStatusRepository.save(taskStatusToUpdate);
        taskStatusDto.setId(taskStatusToUpdate.getId());
        taskStatusDto.setCreatedAt(taskStatusToUpdate.getCreatedAt());
        return taskStatusDto;
    }

    @Override
    public void deleteStatus(Long statusId) {
        taskStatusRepository.findById(statusId).orElseThrow();
        taskStatusRepository.deleteById(statusId);
    }

    private TaskStatusDto toTaskStatusDto(TaskStatus taskStatus) {
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setName(taskStatus.getName());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDto;
    }
}
