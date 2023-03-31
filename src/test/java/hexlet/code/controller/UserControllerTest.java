package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.config.security.WebSecurityConfig.LOGIN;
import static hexlet.code.utils.TestUtils.DEFAULT_USER_LOGIN_REQUEST;
import static hexlet.code.utils.TestUtils.DEFAULT_USER_USERNAME;
import static hexlet.code.utils.TestUtils.SIZE_OF_EMPTY_REPOSITORY;
import static hexlet.code.utils.TestUtils.SIZE_OF_ONE_ITEM_REPOSITORY;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)


public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach void clear() {
        utils.tearDown();
    }

    @Test
    void registration() throws Exception {
        assertThat(userRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
        utils.regDefaultUser().andExpect(status().isCreated());
        assertThat(userRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);
    }

    @Test
//    @WithMockUser(username="admin",roles={"USER","ADMIN"})
//    @WithUserDetails(value="customUsername", userDetailsServiceBeanName="myUserDetailsService")
    void loginUser() throws Exception {
        utils.regDefaultUser();
        final var loginRequest = post(LOGIN).content(DEFAULT_USER_LOGIN_REQUEST).contentType(APPLICATION_JSON);
        utils.perform(loginRequest)
                .andExpect(status().isOk());
    }

    @Test
    void loginUserFails() throws Exception {
        utils.regDefaultUser();
        final String userWrongLoginJsonRequest = """
            {
                "email": "john@google.com",
                "password": "password"
            }
            """;
        final var loginRequest = post(LOGIN).content(userWrongLoginJsonRequest).contentType(APPLICATION_JSON);
        utils.perform(loginRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById() throws Exception {
        utils.regDefaultUser();

        final User expectedUser = userRepository.findAll().get(0);

        final var response = utils.performAuthorizedRequest(
                get(USER_CONTROLLER_PATH + ID, expectedUser.getId())
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(expectedUser.getId()).isEqualTo(user.getId());
        assertThat(expectedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(expectedUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(expectedUser.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    void getUserByIdFails() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        utils.performAuthorizedRequest(
                get(USER_CONTROLLER_PATH + ID, expectedUser.getId() + 1)
                )
                .andExpect(status().isNotFound());
    }

    @Test
   void getAllUsers() throws Exception {
        utils.regDefaultUser();
        final var response = utils.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }

    @Test
    void twiceRegTheSameUserFail() throws Exception {
        utils.regDefaultUser();

        utils.regDefaultUser().andExpect(status().isUnprocessableEntity());
        assertThat(userRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);
    }

    @Test
    public void updateUser() throws Exception {
        utils.regDefaultUser();
        final String userUpdateJsonRequest = """
            {
                "email": "john@google.com",
                "firstName": "John",
                "lastName": "Petrov",
                "password": "password-another"
            }
            """;

        final Long userId = userRepository.findByEmail(DEFAULT_USER_USERNAME).get().getId();

        utils.performAuthorizedRequest(
                put(USER_CONTROLLER_PATH + ID, userId)
                        .content(userUpdateJsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertThat(userRepository.existsById(userId)).isTrue();
        assertNull(userRepository.findByEmail(DEFAULT_USER_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail("john@google.com").orElse(null));
    }

    @Test
    public void deleteUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(DEFAULT_USER_USERNAME).get().getId();

        utils.performAuthorizedRequest(
                delete(USER_CONTROLLER_PATH + ID, userId))
                .andExpect(status().isOk()
                );

        assertThat(userRepository.count()).isEqualTo(SIZE_OF_EMPTY_REPOSITORY);
    }

    @Test
    public void deleteUserFails() throws Exception {
        utils.regDefaultUser();
        final String newUserCreateRequest = """
            {
                "email": "john@google.com",
                "firstName": "John",
                "lastName": "Petrov",
                "password": "password-another"
            }
            """;

        utils.regNewInstance(USER_CONTROLLER_PATH, newUserCreateRequest);

        final Long defaultUserId = userRepository.findByEmail(DEFAULT_USER_USERNAME).get().getId();
        final Long newUserId = userRepository.findByEmail("john@google.com").get().getId();

        utils.performAuthorizedRequest(
                delete(USER_CONTROLLER_PATH + ID, defaultUserId))
                .andExpect(status().isOk()
                ); //Authorized user was successfully removed from storage

        //Only 1 (new user) remained in repository
        assertThat(userRepository.count()).isEqualTo(SIZE_OF_ONE_ITEM_REPOSITORY);

        utils.performAuthorizedRequest(
                delete(USER_CONTROLLER_PATH + ID, newUserId))
                .andExpect(status().isForbidden()); //Authorized default user may not delete another user.
    }
}
