package workoutlog.model;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutTable(
                Long id,
                String description,
                List<ExerciseTable> exercises,
                LocalDateTime start,
                LocalDateTime end) {

}
