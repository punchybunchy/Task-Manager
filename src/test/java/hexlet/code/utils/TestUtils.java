package hexlet.code.utils;


import hexlet.code.component.JWTHelper;
import hexlet.code.exceptionsHandler.UserNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String defaultUserCreateRequest = """
            {
                "email": "ivan@google.com",
                "firstName": "Ivan",
                "lastName": "Petrov",
                "password": "password"
            }
            """;

    public static final String defaultUserLoginRequest = """
            {
                "email": "ivan@google.com",
                "password": "password"
            }
            """;

    public static final String defaultUserUsername = "ivan@google.com";

    public static final String defaultStatusCreateRequest = """
            {
                "name": "Default Status"
            }
            """;

    public static final String defaultTaskCreateRequest = """
            {
                "name": "New task",
                "description": "Task description",
                "executorId": 1,
                "taskStatusId": 1
            }
            """;

    public static final String defaultLabelCreateRequest = """
            {
                "name": "Default label"
            }
            """;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JWTHelper jwtHelper;

    @Autowired
    TaskStatusRepository taskStatusRepository;


    public void tearDown() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regNewUser(defaultUserCreateRequest);
    }

    public ResultActions regDefaultStatus() throws Exception {
        return regNewStatus(defaultStatusCreateRequest);
    }

    public ResultActions regDefaultTask() throws Exception {
        return regNewTask(defaultTaskCreateRequest);
    }

    public ResultActions regDefaultLabel() throws Exception {
        return regNewLabel(defaultLabelCreateRequest);
    }

    public ResultActions regNewUser(final String userCreateJsonRequest) throws Exception {
        final MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(userCreateJsonRequest)
                .contentType(MediaType.APPLICATION_JSON);
        return perform(request);
    }


    public ResultActions regNewStatus(final String statusCreateJsonRequest) throws Exception {
        return getAuthorizedRequest(post(STATUS_CONTROLLER_PATH)
                .content(statusCreateJsonRequest)
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions regNewTask(final String taskCreateJsonRequest) throws Exception {
        return getAuthorizedRequest(post(TASK_CONTROLLER_PATH)
                .content(taskCreateJsonRequest)
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions regNewLabel(final String labelCreateJsonRequest) throws Exception {
        return getAuthorizedRequest(post(LABEL_CONTROLLER_PATH)
                .content(labelCreateJsonRequest)
                .contentType(MediaType.APPLICATION_JSON));
    }

    //----

    public ResultActions getAuthorizedRequest(final MockHttpServletRequestBuilder request) throws Exception {
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, defaultUserUsername));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions getAuthorizedRequest(final MockHttpServletRequestBuilder request, String newUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, newUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    //----

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    //----


    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }


}
