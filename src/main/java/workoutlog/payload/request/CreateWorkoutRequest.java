package workoutlog.payload.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateWorkoutRequest(
        @NotBlank String description,
        @NotNull LocalDateTime start,
        @NotNull @NotEmpty List<CreateExerciseRequest> exercises,
        LocalDateTime end) {

}
