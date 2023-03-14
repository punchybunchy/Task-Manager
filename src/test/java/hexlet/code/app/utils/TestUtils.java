package hexlet.code.app.utils;


import hexlet.code.app.dto.UserDto;
import hexlet.code.app.exceptionsHandler.UserNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static hexlet.code.app.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "petr@yahoo.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    private final String email = TEST_USERNAME;
    private final String firstName = "Petr";
    private final String lastName = "Petrov";
    private final String password = "password";

    private final UserDto testRegistrationDto = new UserDto(email,firstName, lastName, password);

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    public void tearDown() {
        userRepository.deleteAll();
    }

    public User getUserById(final Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User ot found"));
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }


}
