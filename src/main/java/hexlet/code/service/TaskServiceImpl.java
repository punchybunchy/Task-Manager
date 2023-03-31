package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.interfaces.LabelService;
import hexlet.code.service.interfaces.TaskService;
import hexlet.code.service.interfaces.TaskStatusService;
import hexlet.code.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final LabelService labelService;
    private final TaskStatusService taskStatusService;
    private final UserService userService;


    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public List<Task> getAllTasks(Predicate predicate) {
        return (List<Task>) taskRepository.findAll(predicate);
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        return taskRepository.save(buildTask(taskDto));
    }

    @Override
    public Task updateTask(TaskDto taskDto, Long id) {
        Task temporaryTask = buildTask(taskDto);
        final Task task = taskRepository.findById(id).get();
        task.setName(temporaryTask.getName());
        task.setDescription(temporaryTask.getDescription());
        task.setExecutor(temporaryTask.getExecutor());
        task.setTaskStatus(temporaryTask.getTaskStatus());
        task.setLabels(temporaryTask.getLabels());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        final Task task = taskRepository.findById(id).get();
        taskRepository.delete(task);
    }

    private Task buildTask(TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        final User executor = Optional.ofNullable(taskDto.getExecutorId())
                .map(userService::getUserById)
                .orElse(null);

        final TaskStatus status = Optional.ofNullable(taskDto.getTaskStatusId())
                .map(taskStatusService::getStatus)
                .orElse(null);


        final Set<Label> labels = Optional.ofNullable(taskDto.getLabelIds())
                .orElse(Set.of())
                .stream()
                .filter(Objects::nonNull)
                .map(id -> labelService.getLabelById(id))
                .collect(Collectors.toSet());

        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .taskStatus(status)
                .author(author)
                .executor(executor)
                .labels(labels)
                .build();
    }

}
