package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
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
import static hexlet.code.utils.TestUtils.SIZE_OF_EMPTY_REPOSITORY;
import static hexlet.code.utils.TestUtils.SIZE_OF_ONE_ITEM_REPOSITORY;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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
    void testRegNewStatus() throws Exception {
        utils.regDefaultUser();
        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
        TaskStatusDto statusDto = new TaskStatusDto("Default status");
        var response = utils.performAuthorizedRequest(
                post(STATUS_CONTROLLER_PATH)
                        .content(asJson(statusDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final TaskStatus status = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);
        assertThat(status.getName()).isEqualTo(statusDto.getName());
    }

    @Test
    void testGetStatusById() throws Exception {
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
    void testGetStatusByIdFails() throws Exception {
        utils.regDefaultUser();
        utils.regDefaultStatus();
        final TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.performAuthorizedRequest(
                get(STATUS_CONTROLLER_PATH + ID, expectedStatus.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStatuses() throws Exception {
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
    void testUpdateStatus() throws Exception {
        utils.regDefaultUser();
        var response = utils.regDefaultStatus()
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final TaskStatus oldStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final Long statusId = oldStatus.getId();

        TaskStatusDto newStatus = new TaskStatusDto("New Status");

        utils.performAuthorizedRequest(
                put(STATUS_CONTROLLER_PATH + ID, statusId)
                        .content(asJson(newStatus))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(statusId)).isTrue();
        assertNull(taskStatusRepository.findByName(oldStatus.getName()).orElse(null));
        assertNotNull(taskStatusRepository.findByName(newStatus.getName()).orElse(null));
    }

    @Test
    void testDeleteStatus() throws Exception {
        utils.regDefaultUser();
        var response = utils.regDefaultStatus()
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final TaskStatus status = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final Long statusId = status.getId();

        utils.performAuthorizedRequest(
                delete(STATUS_CONTROLLER_PATH + ID, statusId))
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
    }
}
