package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.interfaces.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus getStatus(long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Override
    public List<TaskStatus> getStatuses() {
        return taskStatusRepository.findAll();
    }

    @Override
    public TaskStatus createStatus(TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        taskStatusRepository.save(taskStatus);
        return taskStatus;
    }

    @Override
    public TaskStatus updateStatus(TaskStatusDto taskStatusDto, long id) {
        final TaskStatus taskStatus = getStatus(id);
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public void deleteStatus(long id) {
        final TaskStatus taskStatus = getStatus(id);
        taskStatusRepository.delete(taskStatus);
    }
}
