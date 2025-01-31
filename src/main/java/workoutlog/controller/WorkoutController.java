package workoutlog.controller;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import workoutlog.model.UserTable;
import workoutlog.model.WorkoutTable;
import workoutlog.payload.request.CreateWorkoutRequest;
import workoutlog.payload.response.CreateWorkoutResponse;
import workoutlog.service.AuthenticationUserRetriverService;
import workoutlog.service.WorkoutService;

@RestController
@RequestMapping("/workout")
public record WorkoutController(
        WorkoutService workoutService,
        AuthenticationUserRetriverService authenticationUserRetriverService) {

    @PostMapping
    public ResponseEntity<CreateWorkoutResponse> create(@RequestBody CreateWorkoutRequest request) {
        return workoutService
                .create(request)
                .map(w -> ResponseEntity.status(HttpStatus.CREATED).body(w))
                .orElseThrow();
    }

    @GetMapping
    public ResponseEntity<Collection<WorkoutTable>> get(Authentication auth) {
        UserTable user = authenticationUserRetriverService.retrieve(auth);
        Collection<WorkoutTable> workouts = workoutService.getAll(user);
        if (!workouts.isEmpty()) {
            return ResponseEntity.ok(workouts);
        }
        return ResponseEntity.notFound().build();
    }

}
