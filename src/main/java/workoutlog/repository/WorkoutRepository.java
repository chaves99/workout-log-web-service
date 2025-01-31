package workoutlog.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import workoutlog.model.UserTable;
import workoutlog.model.WorkoutTable;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkoutRepository {

    private final JdbcTemplate jdbcTemplate;

    private final static String TABLE_NAME = "workout";

    public Optional<WorkoutTable> save(WorkoutTable workout) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement("INSERT INTO " + TABLE_NAME + "(description, start, ending) values(?, ?, ?)");
            ps.setString(1, workout.description());
            ps.setTimestamp(2, Timestamp.from(workout.start().toInstant(ZoneOffset.UTC)));
            ps.setTimestamp(2, Timestamp.from(workout.end().toInstant(ZoneOffset.UTC)));
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        return findById(id);
    }

    public Optional<WorkoutTable> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        WorkoutTable workout = jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, id);
            return ps;
        }, rs -> {
            if (rs.next()) {
                return resultSetToWorkout(rs);
            }
            return null;
        });

        return Optional.ofNullable(workout);
    }

    public Collection<WorkoutTable> findByUser(UserTable user) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";
        Collection<WorkoutTable> workouts = jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, user.id());
            return ps;
        }, rs -> {
            Collection<WorkoutTable> workoutCollection = new ArrayList<>();
            while (rs.next()) {
                workoutCollection.add(resultSetToWorkout(rs));
            }
            return Collections.emptyList();
        });
        return workouts;
    }

    private WorkoutTable resultSetToWorkout(ResultSet rs) throws SQLException {
        return new WorkoutTable(rs.getLong(1),
                rs.getString(2),
                null,
                LocalDateTime.ofInstant(rs.getDate(3).toInstant(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(rs.getDate(4).toInstant(), ZoneOffset.UTC));
    }
}
