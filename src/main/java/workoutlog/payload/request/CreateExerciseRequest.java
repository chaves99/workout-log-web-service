package workoutlog.payload.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateExerciseRequest(
        @NotBlank String name,
        @NotNull Integer order,
        @NotNull @NotEmpty List<CreateExecutionRequest> executions) {

}
