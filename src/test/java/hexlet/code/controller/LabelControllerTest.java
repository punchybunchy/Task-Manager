package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.utils.TestUtils.defaultLabelCreateRequest;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc

public class LabelControllerTest {
    @Autowired
    LabelRepository labelRepository;

    @Autowired
    TestUtils utils;

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
    public void regNewLabel() throws Exception {

        assertThat(labelRepository.count()).isEqualTo(sizeOfEmptyRepository);

        var response = utils.getAuthorizedRequest(
                post(LABEL_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(defaultLabelCreateRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Label>() {});
        assertThat(label.getName()).isEqualTo("Default label");
        assertThat(labelRepository.count()).isEqualTo(sizeOfOneItemRepository);
    }

    @Test
    public void getLabel() throws Exception {
        utils.regDefaultLabel();
        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = utils.getAuthorizedRequest(
                        get(LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(label.getId()).isEqualTo(expectedLabel.getId());
        Assertions.assertThat(label.getName()).isEqualTo(expectedLabel.getName());
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.regDefaultLabel();
        assertThat(labelRepository.count()).isEqualTo(sizeOfOneItemRepository);

        final String newLabel = """
            {
                "name": "New label"
            }
            """;
        utils.regNewInstance(LABEL_CONTROLLER_PATH, newLabel);

        assertThat(labelRepository.count()).isEqualTo(sizeOfOneItemRepository + sizeOfOneItemRepository);

        final var response = utils.getAuthorizedRequest(
                        get(LABEL_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(labels.get(0).getName()).isEqualTo("Default label");
        Assertions.assertThat(labels.get(1).getName()).isEqualTo("New label");
    }

    @Test
    public void updateLabel() throws Exception {
        utils.regDefaultLabel();
        final String labelUpdateJsonRequest = """
            {
                "name": "Updated label"
            }
            """;

        final Long labelId = labelRepository.findAll().get(0).getId();

        var response = utils.getAuthorizedRequest(
                put(LABEL_CONTROLLER_PATH + ID, labelId)
                        .content(labelUpdateJsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Label updatedLabel = fromJson(response.getContentAsString(), new TypeReference<>() {});

        Assertions.assertThat(updatedLabel.getName()).isEqualTo("Updated label");
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultLabel();
        assertThat(labelRepository.count()).isEqualTo(sizeOfOneItemRepository);

        final Long defaultLabelId = labelRepository.findAll().get(0).getId();

        utils.getAuthorizedRequest(
                        delete(LABEL_CONTROLLER_PATH + ID, defaultLabelId))
                .andExpect(status().isOk());

        Assertions.assertThat(labelRepository.count()).isEqualTo(sizeOfEmptyRepository);
    }

}
