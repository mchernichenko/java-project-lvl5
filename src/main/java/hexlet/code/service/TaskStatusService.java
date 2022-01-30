package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusDto> getAllStatus();

    TaskStatusDto getStatusById(Long statusId);

    TaskStatusDto createStatus(TaskStatusDto taskStatusDto);

    TaskStatusDto updateStatus(Long statusId, TaskStatusDto taskStatusDto);

    void deleteStatus(Long statusId);
}
