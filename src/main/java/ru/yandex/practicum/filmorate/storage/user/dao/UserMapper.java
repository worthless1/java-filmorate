package ru.yandex.practicum.filmorate.storage.user.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User>  {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("User_id"))
                .email(rs.getString("Email"))
                .login(rs.getString("Login"))
                .name(rs.getString("Name"))
                .birthday(rs.getDate("Birthday").toLocalDate())
                .build();
    }

}
