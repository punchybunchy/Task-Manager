package hexlet.code.app.service.interfaces;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {
    TaskStatus getStatus(long id);
    List<TaskStatus> getStatuses();
    TaskStatus createStatus(TaskStatusDto taskStatusDto);
    TaskStatus updateStatus(TaskStatusDto taskStatusDto, long id);
    void deleteStatus(long id);
}
