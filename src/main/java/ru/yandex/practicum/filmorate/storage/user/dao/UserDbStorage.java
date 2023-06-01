package ru.yandex.practicum.filmorate.storage.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.film.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.excepton.user.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    KeyHolder keyHolder = new GeneratedKeyHolder();

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Users")
                .usingGeneratedKeyColumns("user_id");

        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(Math.toIntExact(userId));

        return user;
    }

    @Override
    public User updateUser(User user) {
        //checking if the user exists
        List<Integer> userIds = jdbcTemplate.queryForList(
                "SELECT * FROM Users WHERE user_id=?", Integer.class, user.getId());

        if (userIds.isEmpty()) {
            throw new UserDoesntExistException("User with id: " + user.getId() + " does not exist");
        }
        //update
        jdbcTemplate.update("UPDATE Users SET Email=?, Login=?, Name=?, Birthday=? WHERE User_id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM Users", new UserMapper());
    }

    @Override
    public User getUserById(int id) {
        return jdbcTemplate.query("SELECT * FROM Users WHERE User_id=?", new UserMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new UserDoesntExistException("User with id: " + id + " does not exists"));
    }

    @Override
    public void addFriend(int id, int friendId) {
        jdbcTemplate.update("INSERT INTO User_friends (user_id, friend_id, status) VALUES (?, ?, false)",
                id, friendId);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        jdbcTemplate.update("DELETE FROM User_friends WHERE user_id=? and friend_id=?",
                id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN user_friends uf ON u.user_id = uf.friend_id " +
                "WHERE uf.user_id = ? " +
                "AND uf.status = true";

        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN user_friends uf1 ON u.user_id = uf1.friend_id " +
                "JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? " +
                "AND uf2.user_id = ? " +
                "AND uf1.status = true " +
                "AND uf2.status = true";

        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

}
