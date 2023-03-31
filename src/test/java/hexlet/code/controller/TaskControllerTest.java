package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.SIZE_OF_EMPTY_REPOSITORY;
import static hexlet.code.utils.TestUtils.SIZE_OF_ONE_ITEM_REPOSITORY;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerTest {

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;


    private static final String DEFAULT_TASK_TITLE = "Default task title";
    private static final String DEFAULT_TASK_DESCRIPTION = "Default task description";

    @BeforeEach
    void prepareDefaults() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        utils.regDefaultLabel();
    }

    @AfterEach
    void clear() {
        utils.tearDown();
    }

    @Test
    void createNewTask() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        assertThat(taskRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);

        utils.performAuthorizedRequest(
                post(TASK_CONTROLLER_PATH)
                        .content(utils.asJson(defaultTask))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        Assertions.assertThat(taskRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);
    }

    
    @Test
    void getTaskById() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);

        final Task expectedTask = taskRepository.findAll().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .get();

        final var response = utils.performAuthorizedRequest(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });

        Assertions.assertThat(expectedTask.getId()).isEqualTo(task.getId());
        Assertions.assertThat(expectedTask.getName()).isEqualTo(task.getName());
    }

    @Test
    void getTaskByIdFails() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);

        final Task expectedTask = taskRepository.findAll().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .get();

        final var response = utils.performAuthorizedRequest(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);

        final List<Task> expectedTasks = taskRepository.findAll();

        final var response = utils.performAuthorizedRequest(
                get(TASK_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });

        Assertions.assertThat(tasks).hasSize(SIZE_OF_ONE_ITEM_REPOSITORY);
        Assertions.assertThat(expectedTasks.get(0).getName()).isEqualTo(tasks.get(0).getName());
    }


    @Test
    void updateTask() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);

        final Task expectedTask = taskRepository.findAll().stream()
                .filter(Objects::nonNull)
                .findFirst()
                .get();

        final String updatedTaskRequest = String.format("""
            {
                "name": "Another task",
                "description": "Another task description",
                "executorId": %d,
                "taskStatusId": %d
            }
            """, defaultTask.getExecutorId(), defaultTask.getAuthorId());

        var response = utils.performAuthorizedRequest(
                put(TASK_CONTROLLER_PATH + ID, expectedTask.getId())
                        .content(updatedTaskRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Task updatedTask = fromJson(response.getContentAsString(), new TypeReference<>() { });

        Assertions.assertThat(updatedTask.getName()).isEqualTo("Another task");
        Assertions.assertThat(updatedTask.getDescription()).isEqualTo("Another task description");
        Assertions.assertThat(taskRepository.findById(expectedTask.getId()).get().getName()).isEqualTo("Another task");
    }


    @Test
    void deleteTaskByOwner() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);
        assertThat(taskRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);

        final Long defaultTaskId = taskRepository.findAll().get(0).getId();

        utils.performAuthorizedRequest(
                delete(TASK_CONTROLLER_PATH + ID, defaultTaskId))
                .andExpect(status().isOk());

        Assertions.assertThat(taskRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
    }


    @Test
    void deleteTaskByNotOwnerFail() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);
        assertThat(taskRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);

        final Long defaultTaskId = taskRepository.findAll().get(0).getId();

        String newUserUsername = "IAmNewUser";

        utils.performAuthorizedRequest(
                delete(TASK_CONTROLLER_PATH + ID, defaultTaskId), newUserUsername)
                .andExpect(status().isForbidden());
    }

    @Test
    void getFilteredRequest() throws Exception {
        final TaskDto defaultTask = buildTaskDto();
        performAuthorizedTaskRequest(defaultTask);

        String queryRequest = String.format("/tasks?taskStatus=%d&executorId=%d&labels=%d",
                defaultTask.getTaskStatusId(),
                defaultTask.getExecutorId(),
                defaultTask.getLabelIds().stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .get());

        final var response = utils.performAuthorizedRequest(
                        get(queryRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(tasks.get(0).getTaskStatus().getId()).isEqualTo(defaultTask.getTaskStatusId());
        assertThat(tasks.get(0).getExecutor().getId()).isEqualTo(defaultTask.getExecutorId());


    }

    private TaskDto buildTaskDto() {

        User defaultUser = userRepository.findAll().stream().filter(Objects::nonNull).findFirst().get();
        TaskStatus defaultStatus = taskStatusRepository.findAll().stream().filter(Objects::nonNull).findFirst().get();
        Label defaultLabel = labelRepository.findAll().stream().filter(Objects::nonNull).findFirst().get();
        return  new TaskDto(
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                defaultUser.getId(),
                defaultUser.getId(),
                defaultStatus.getId(),
                Set.of(defaultLabel.getId())
        );
    }

    private ResultActions performAuthorizedTaskRequest(TaskDto taskDto) throws Exception {
        return utils.performAuthorizedRequest(
                post(TASK_CONTROLLER_PATH)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON));
    }
}
