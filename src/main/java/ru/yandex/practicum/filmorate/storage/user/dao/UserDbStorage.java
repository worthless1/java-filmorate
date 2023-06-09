package ru.yandex.practicum.filmorate.storage.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.user.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Users")
                .usingGeneratedKeyColumns("user_id");

        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        //checking if the user exists
        List<Integer> userIds = jdbcTemplate.queryForList(
                "SELECT user_id FROM Users WHERE user_id=?", Integer.class, user.getId());

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
        List<User> users = jdbcTemplate.query("SELECT * FROM Users", new UserMapper());
        users.forEach(this::addFriendsToUser);

        return users;
    }

    @Override
    public User getUserById(int id) {
        User user = jdbcTemplate.query("SELECT * FROM Users WHERE User_id=?", new UserMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new UserDoesntExistException("User with id: " + id + " does not exists"));

        addFriendsToUser(user);

        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        //check existing user
        getUserById(id);
        getUserById(friendId);

        jdbcTemplate.update("INSERT INTO User_friends (user_id, friend_id, status) VALUES (?, ?, false)",
                id, friendId);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        //check existing user
        getUserById(id);
        getUserById(friendId);

        jdbcTemplate.update("DELETE FROM User_friends WHERE user_id=? and friend_id=?",
                id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        //check existing user
        getUserById(id);

        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN user_friends uf ON u.user_id = uf.friend_id " +
                "WHERE uf.user_id = ? ";

        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        //check existing user
        getUserById(id);
        getUserById(otherId);

        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN user_friends uf1 ON u.user_id = uf1.friend_id " +
                "JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? " +
                "AND uf2.user_id = ? " +
                "AND uf1.friend_id = uf2.friend_id ";

        return jdbcTemplate.query(sql, new UserMapper(), id, otherId);
    }

    private void addFriendsToUser(User user) {
        String sql = "SELECT user_id, status " +
                "FROM user_friends " +
                "WHERE user_id=?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, user.getId());

        Map<Integer, Boolean> friends = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Integer userId = (Integer) row.get("user_id");
            Boolean status = (Boolean) row.get("status");
            friends.put(userId, status);
        }
        user.setFriends(friends);
    }

}
