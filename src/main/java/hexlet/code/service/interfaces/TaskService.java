package hexlet.code.service.interfaces;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    Task createTask(TaskDto taskDto);
    Task updateTask(TaskDto taskDto, Long id);
    void deleteTask(Long id);

}
