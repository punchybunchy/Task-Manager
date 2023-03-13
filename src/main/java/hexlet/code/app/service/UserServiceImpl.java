package hexlet.code.app.service;

import hexlet.code.app.dto.UserDtoRequest;
import hexlet.code.app.dto.UserDtoResponse;
import hexlet.code.app.exceptionsHandler.UserNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.interfaces.UserService;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static hexlet.code.app.config.security.WebSecurityConfig.DEFAULT_AUTHORITIES;


@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDtoResponse getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found. Invalid user ID: " + id));
        return new UserDtoResponse(
                id,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt()
        );
    }

    @Override
    public List<UserDtoResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    return new UserDtoResponse(
                            user.getId(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getCreatedAt()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    public UserDtoResponse createUser(UserDtoRequest userDtoRequest) {
        final User user = new User();
        user.setEmail(userDtoRequest.getEmail());
        user.setFirstName(userDtoRequest.getFirstName());
        user.setLastName(userDtoRequest.getLastName());
        String password = userDtoRequest.getPassword();
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));
        userRepository.save(user);

        long id = user.getId();
        Date createdAt = user.getCreatedAt();
        return new UserDtoResponse(
                id,
                userDtoRequest.getEmail(),
                userDtoRequest.getFirstName(),
                userDtoRequest.getLastName(),
                createdAt
        );
    }

    @Override
    public UserDtoResponse updateUser(UserDtoRequest userDtoRequest, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found. Invalid user ID: " + id));
        user.setEmail(userDtoRequest.getEmail());
        user.setFirstName(userDtoRequest.getFirstName());
        user.setLastName(userDtoRequest.getLastName());
        String password = userDtoRequest.getPassword();
        user.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));
        userRepository.save(user);

        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lasttName = user.getLastName();
        Date date = user.getCreatedAt();
        String passwordUser = user.getPassword();

        return new UserDtoResponse(
                id,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt()
        );
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found. Invalid user ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::buildSpringUser)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user with 'username': " + username));
    }

    private UserDetails buildSpringUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                DEFAULT_AUTHORITIES
        );
    }

}
