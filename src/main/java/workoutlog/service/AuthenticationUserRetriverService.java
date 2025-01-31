package workoutlog.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import workoutlog.model.UserTable;
import workoutlog.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationUserRetriverService {

    private final UserRepository userRepository;

    public UserTable retrieve(Authentication authentication) {
        User userDetail = (User) authentication.getPrincipal();
        return userRepository.findByEmail(userDetail.getUsername()).orElseThrow();
    }
}
