package workoutlog.controller;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import workoutlog.model.UserTable;
import workoutlog.payload.request.CreateUserRequest;
import workoutlog.service.UserService;

@RestController
@RequestMapping("/user")
public record UserController(UserService userService) {

    @GetMapping("/{id}")
    public ResponseEntity<UserTable> get(@PathVariable Long id) throws SQLException {
        UserTable user = userService.findById(id).orElseThrow();
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserTable> create(@RequestBody CreateUserRequest user) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user).orElseThrow());
    }

}
