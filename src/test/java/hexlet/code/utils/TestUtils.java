package hexlet.code.utils;

import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
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
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    private UserDto defaultUser = new UserDto(
            DEFAULT_USER_USERNAME,
            "Ivan",
            "Petrov",
            "password");

    private TaskStatusDto defaultStatus = new TaskStatusDto("Default Status");

    private LabelDto defaultLabel = new LabelDto("Default label");

    public static final String DEFAULT_USER_USERNAME = "ivan@google.com";
    public static final String ANOTHER_USER_USERNAME = "john@google.com";

    public static final int SIZE_OF_EMPTY_REPOSITORY = 0;
    public static final int SIZE_OF_ONE_ITEM_REPOSITORY = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTHelper jwtHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskRepository taskRepository;



    public void tearDown() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        taskRepository.deleteAll();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regNewInstance(USER_CONTROLLER_PATH, defaultUser);
    }

    public ResultActions regDefaultStatus() throws Exception {
        return regNewInstance(STATUS_CONTROLLER_PATH, defaultStatus);
    }

    public ResultActions regDefaultLabel() throws Exception {
        return regNewInstance(LABEL_CONTROLLER_PATH, defaultLabel);
    }

    public ResultActions regNewInstance(String path, Object userDto) throws Exception {
        return performAuthorizedRequest(post(path)
                .content(asJson(userDto))
                .contentType(MediaType.APPLICATION_JSON));
    }

    //----

    public ResultActions performAuthorizedRequest(final MockHttpServletRequestBuilder request) throws Exception {
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, DEFAULT_USER_USERNAME));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions performAuthorizedRequest(
            final MockHttpServletRequestBuilder request, String newUser) throws Exception {
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

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
