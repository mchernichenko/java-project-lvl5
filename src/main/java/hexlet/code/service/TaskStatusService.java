package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatus> getAllStatus();

    TaskStatus getStatusById(Long statusId);

    TaskStatus createStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateStatus(Long statusId, TaskStatusDto taskStatusDto);

    void deleteStatus(Long statusId);
}
