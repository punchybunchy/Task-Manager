package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.config.SpringConfigForIT;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hexlet.code.app.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.app.controller.TaskStatusController.ID;
import static hexlet.code.app.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.app.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
@Transactional

public class TaskStatusControllerTest {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    private static final int sizeOfEmptyRepository = 0;
    private static final int sizeOfOneItemRepository = 1;


    @Test
    public void createNewTaskStatus() throws Exception {
        utils.regDefaultUser();
        assertThat(taskStatusRepository.count()).isEqualTo(sizeOfEmptyRepository);
        utils.getAuthorizedRequest(
                post(STATUS_CONTROLLER_PATH)
                        .content(defaultStatusCreateRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated());
        assertThat(taskStatusRepository.count()).isEqualTo(sizeOfOneItemRepository);
    }

    @Test
    public void getStatusById() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.getAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertThat(expectedStatus.getId()).isEqualTo(taskStatus.getId());
        assertThat(expectedStatus.getName()).isEqualTo(taskStatus.getName());

    }

    @Test
    public void getStatusByIdFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.getAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId() + 1)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllStatuses() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final var response = utils.getAuthorizedRequest(
                        get(STATUS_CONTROLLER_PATH)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void updateStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final String statusUpdateJsonRequest = """
            {
                "name": "New Status"
            }
            """;

        final Long statusId = taskStatusRepository.findByName("Default Status").get().getId();

        utils.getAuthorizedRequest(
                put(STATUS_CONTROLLER_PATH + ID, statusId)
                        .content(statusUpdateJsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(statusId)).isTrue();
        assertNull(taskStatusRepository.findByName("Default Status").orElse(null));
        assertNotNull(taskStatusRepository.findByName("New Status").orElse(null));
    }

    @Test
    public void deleteStatus() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();

        final Long statusId = taskStatusRepository.findByName("Default Status").get().getId();

        utils.getAuthorizedRequest(
                        delete(STATUS_CONTROLLER_PATH + ID, statusId))
                .andExpect(status().isOk()
                );

        assertThat(taskStatusRepository.count()).isEqualTo(sizeOfEmptyRepository);
    }

}
