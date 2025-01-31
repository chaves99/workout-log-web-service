package workoutlog.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import workoutlog.model.ExecutionTable;
import workoutlog.model.ExerciseTable;
import workoutlog.model.WorkoutTable;
import workoutlog.payload.request.CreateExecutionRequest;
import workoutlog.payload.request.CreateWorkoutRequest;
import workoutlog.payload.response.CreateWorkoutResponse;

@Component
public record WorkoutMapper() {

    public CreateWorkoutResponse map(WorkoutTable table) {
        return new CreateWorkoutResponse(table.id());
    }

    public WorkoutTable map(CreateWorkoutRequest request) {
        request.exercises().stream().map(ex -> {
            return new ExerciseTable(
                    ex.name(),
                    ex.order(),
                    mapExecutions(ex.executions()));
        });

        return new WorkoutTable(
                null,
                request.description(),
                null,
                request.start(),
                request.end());
    }

    private List<ExecutionTable> mapExecutions(List<CreateExecutionRequest> executions) {
        return executions.stream()
                .map(execution -> {
                    return new ExecutionTable(
                            execution.reps(),
                            execution.weight(),
                            execution.order());
                }).toList();
    }

}
