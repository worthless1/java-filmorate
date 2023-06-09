package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {


    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO Users (Email, Login, Name, Birthday) VALUES (?, ?, ?, ?)",
                "test@test.com", "login", "name", LocalDate.of(1999, 12, 12));

        jdbcTemplate.update("INSERT INTO Users (Email, Login, Name, Birthday) VALUES (?, ?, ?, ?)",
                "test2@test.com", "login2", "name2", LocalDate.of(1992, 12, 12));
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM Users");
        jdbcTemplate.update("ALTER TABLE Users ALTER COLUMN User_id RESTART WITH 1");
    }

    @Test
    void testCreateUser() {
        User user = User.builder()
                .email("create@test.com")
                .login("loginCreate")
                .name("nameCreate")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userStorage.createUser(user);

        User testUser = userStorage.getUserById(3);

        assertEquals("create@test.com", testUser.getEmail());
        assertEquals("loginCreate", testUser.getLogin());
        assertEquals("nameCreate", testUser.getName());
        assertEquals(LocalDate.of(2001, 1, 1), testUser.getBirthday());
    }

    @Test
    void testUpdateUser() {
        User user = User.builder()
                .id(1)
                .email("updated@test.com")
                .login("loginUpdated")
                .name("nameUpdated")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userStorage.updateUser(user);

        User testUser = userStorage.getUserById(1);

        assertEquals("updated@test.com", testUser.getEmail());
        assertEquals("loginUpdated", testUser.getLogin());
        assertEquals("nameUpdated", testUser.getName());
        assertEquals(LocalDate.of(2001, 1, 1), testUser.getBirthday());
    }

    @Test
    void testGetUsers() {
        List<User> userList = userStorage.getUsers();

        assertEquals(2, userList.size());
    }

    @Test
    void testGetUserById() {
        int id = 1;
        User testUser = userStorage.getUserById(id);

        assertEquals("test@test.com", testUser.getEmail());
        assertEquals("login", testUser.getLogin());
        assertEquals("name", testUser.getName());
        assertEquals(LocalDate.of(1999, 12, 12), testUser.getBirthday());
    }

}