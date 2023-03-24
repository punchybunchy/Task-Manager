package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
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

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.*;
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
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;

    private static final int sizeOfEmptyRepository = 0;
    private static final int sizeOfOneItemRepository = 1;

    @BeforeEach
    public void prepareDefaultUserAndStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
    }
    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createNewTask() throws Exception {
        assertThat(taskRepository.count()).isEqualTo(sizeOfEmptyRepository);

        utils.getAuthorizedRequest(
                post(TASK_CONTROLLER_PATH)
                        .content(defaultTaskCreateRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        Assertions.assertThat(taskRepository.count()).isEqualTo(sizeOfOneItemRepository);
    }

//    @Test
//    public void createNewTaskUnauthorizedFails() throws Exception {
//        utils.regDefaultUser();
//        utils.regDefaultStatus();
//        assertThat(taskRepository.count()).isEqualTo(sizeOfEmptyRepository);
//
//        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(TASK_CONTROLLER_PATH)
//                        .content(defaultTaskCreateRequest)
//                        .contentType(APPLICATION_JSON);
//
//        utils.perform(request).andExpect(status().isUnauthorized());
//
//    }

    @Test
    public void getTaskById() throws Exception {
        utils.regDefaultTask();
        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = utils.getAuthorizedRequest(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(expectedTask.getId()).isEqualTo(task.getId());
        Assertions.assertThat(expectedTask.getName()).isEqualTo(task.getName());
    }

    @Test
    public void getTaskByIdFails() throws Exception {
        utils.regDefaultTask();
        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = utils.getAuthorizedRequest(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTasks() throws Exception {
        utils.regDefaultTask();
        final List<Task> expectedTasks = taskRepository.findAll();

        final var response = utils.getAuthorizedRequest(
                get(TASK_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(tasks).hasSize(sizeOfOneItemRepository);
        Assertions.assertThat(expectedTasks.get(0).getName()).isEqualTo(tasks.get(0).getName());
    }

    @Test
    public void updateTask() throws Exception {
        utils.regDefaultTask();

        final Long defaultTaskId = taskRepository.findAll().get(0).getId();

        final String anotherTaskCreateRequest = """
            {
                "name": "Another task",
                "description": "Another task description",
                "executorId": 1,
                "taskStatusId": 1
            }
            """;

        var response = utils.getAuthorizedRequest(
                put(TASK_CONTROLLER_PATH + ID, defaultTaskId)
                        .content(anotherTaskCreateRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Task updatedTask = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(updatedTask.getName()).isEqualTo("Another task");
        Assertions.assertThat(updatedTask.getDescription()).isEqualTo("Another task description");
        Assertions.assertThat(taskRepository.findById(defaultTaskId).get().getName()).isEqualTo("Another task");
    }

    @Test
    public void deleteTaskByOwner() throws Exception {
        utils.regDefaultTask();
        assertThat(taskRepository.count()).isEqualTo(sizeOfOneItemRepository);

        final Long defaultTaskId = taskRepository.findAll().get(0).getId();

        utils.getAuthorizedRequest(
                delete(TASK_CONTROLLER_PATH + ID, defaultTaskId))
                .andExpect(status().isOk());

        Assertions.assertThat(taskRepository.count()).isEqualTo(sizeOfEmptyRepository);
    }

    @Test
    public void deleteTaskByNotOwnerFail() throws Exception {
        utils.regDefaultTask();
        assertThat(taskRepository.count()).isEqualTo(sizeOfOneItemRepository);

        final Long defaultTaskId = taskRepository.findAll().get(0).getId();

        String newUserUsername = "IAmNewUser";

        utils.getAuthorizedRequest(
                delete(TASK_CONTROLLER_PATH + ID, defaultTaskId), newUserUsername)
                .andExpect(status().isForbidden());
    }
}
