package hexlet.code.app.utils;


import hexlet.code.app.component.JWTHelper;
import hexlet.code.app.exceptionsHandler.UserNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
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

import static hexlet.code.app.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.app.controller.UserController.USER_CONTROLLER_PATH;
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

//    public static String asJson(final Object object) throws JsonProcessingException {
//        return MAPPER.writeValueAsString(object);
//    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }


}
