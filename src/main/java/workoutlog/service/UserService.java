package workoutlog.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import workoutlog.model.UserTable;
import workoutlog.payload.request.CreateUserRequest;
import workoutlog.repository.UserRepository;

@Slf4j
@Service
public record UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    public Optional<UserTable> create(CreateUserRequest user) {
        var encodedPassword = passwordEncoder.encode(user.password());
        try {
            return userRepository.create(new UserTable(null, user.name(), encodedPassword, user.email()));
        } catch (Exception e) {
            log.error("create - name:{} email:{} exception:{}", user.name(), user.email(), e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserTable> findById(Long id) {
        try {
            return userRepository.findById(id);

        } catch (Exception e) {
            log.error("create - id:{} exception:{}", id, e.getMessage());
            return Optional.empty();
        }
    }

}
