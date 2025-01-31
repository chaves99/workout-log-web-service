package workoutlog.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import workoutlog.model.UserTable;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final static String TABLE_NAME = "USER_WL";

    private final JdbcTemplate jdbcTemplate;

    public Optional<UserTable> create(UserTable user) throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into " + TABLE_NAME + "(name, password, email) values(?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.name());
            ps.setString(2, user.password());
            ps.setString(3, user.email());
            return ps;
        }, keyHolder);
        Long key = (Long) keyHolder.getKeyList().get(0).get("id");
        return findById(key);
    }

    public Optional<UserTable> findByEmail(String email) {
        String sql = "select * from " + TABLE_NAME + " where email = ?";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            return ps;
        };
        UserTable user = jdbcTemplate.query(preparedStatementCreator, rm -> {
            if (rm.next()) {
                return new UserTable(rm.getLong(1), rm.getString(2), rm.getString(3), rm.getString(4));
            }
            return null;
        });
        return Optional.ofNullable(user);
    }

    public Optional<UserTable> findById(Long id) throws SQLException {
        String sql = "select * from " + TABLE_NAME + " where id = ?";
        PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            return Optional.of(new UserTable(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("password"), resultSet.getString("email")));
        }
        return Optional.empty();
    }

}
