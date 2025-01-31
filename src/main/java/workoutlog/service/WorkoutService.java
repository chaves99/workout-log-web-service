package workoutlog.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import workoutlog.mapper.WorkoutMapper;
import workoutlog.model.UserTable;
import workoutlog.model.WorkoutTable;
import workoutlog.payload.request.CreateWorkoutRequest;
import workoutlog.payload.response.CreateWorkoutResponse;
import workoutlog.repository.WorkoutRepository;

@Slf4j
@Service
public record WorkoutService(
        WorkoutRepository workoutRepository,
        WorkoutMapper workoutMapper) {

    public Optional<CreateWorkoutResponse> create(CreateWorkoutRequest request) {
        log.info("create - request:{}", request);
        WorkoutTable table = workoutMapper.map(request);
        Optional<WorkoutTable> savedWorkout = workoutRepository.save(table);
        return savedWorkout.map(w -> workoutMapper.map(w));
    }

    public Collection<WorkoutTable> getAll(UserTable user) {
        return workoutRepository.findByUser(user);
    }

}
