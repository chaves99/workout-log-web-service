package workoutlog.payload.request;

import jakarta.validation.constraints.NotNull;

public record CreateExecutionRequest(
        @NotNull Integer reps,
        @NotNull Double weight,
        @NotNull Integer order) {
}
