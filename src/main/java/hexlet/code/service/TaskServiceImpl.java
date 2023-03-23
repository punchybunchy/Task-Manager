package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.interfaces.TaskService;
import hexlet.code.service.interfaces.TaskStatusService;
import hexlet.code.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private TaskStatusService taskStatusService;
    private UserService userService;


    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        return taskRepository.save(buildTask(taskDto));
    }

    @Override
    public Task updateTask(TaskDto taskDto, Long id) {
        Task temporyTask = buildTask(taskDto);
        final Task task = taskRepository.findById(id).get();
        task.setName(temporyTask.getName());
        task.setDescription(temporyTask.getDescription());
        task.setExecutor(temporyTask.getExecutor());
        task.setTaskStatus(temporyTask.getTaskStatus());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        final Task task = taskRepository.findById(id).get();
        taskRepository.delete(task);
    }

    private Task buildTask(TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        final User executor = userService.getUserById(taskDto.getExecutorId());
        final TaskStatus status = taskStatusService.getStatus(taskDto.getTaskStatusId());

        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .taskStatus(status)
                .author(author)
                .executor(executor)
                .build();
    }

}
