package workoutlog.model;

import java.util.List;

public record ExerciseTable(
        String name,
        Integer order,
        List<ExecutionTable> executions) {

}
