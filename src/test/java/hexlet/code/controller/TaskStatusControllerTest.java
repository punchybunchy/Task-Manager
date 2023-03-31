package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc


public class TaskStatusControllerTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    void createNewTaskStatus() throws Exception {
        utils.regDefaultUser();
        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
        utils.performAuthorizedRequest(
                post(STATUS_CONTROLLER_PATH)
                        .content(DEFAULT_STATUS_CREATE_REQUEST)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);
    }

    @Test
    void getStatusById() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.performAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(expectedStatus.getId()).isEqualTo(taskStatus.getId());
        assertThat(expectedStatus.getName()).isEqualTo(taskStatus.getName());

    }

    @Test
    void getStatusByIdFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.performAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllStatuses() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final var response = utils.performAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(taskStatuses).hasSize(SIZE_OF_ONE_ITEM_REPOSITORY);
    }

    @Test
    void updateStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final String statusUpdateJsonRequest = """
            {
                "name": "New Status"
            }
            """;

        final Long statusId = taskStatusRepository.findByName("Default Status").get().getId();

        utils.performAuthorizedRequest(
                put(STATUS_CONTROLLER_PATH + ID, statusId)
                        .content(statusUpdateJsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(statusId)).isTrue();
        assertNull(taskStatusRepository.findByName("Default Status").orElse(null));
        assertNotNull(taskStatusRepository.findByName("New Status").orElse(null));
    }

    @Test
    void deleteStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();

        final Long statusId = taskStatusRepository.findByName("Default Status").get().getId();

        utils.performAuthorizedRequest(
                delete(STATUS_CONTROLLER_PATH + ID, statusId))
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
    }
}
