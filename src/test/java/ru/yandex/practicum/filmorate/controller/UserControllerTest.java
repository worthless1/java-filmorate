package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUser_withValidData_returnsCreatedUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getLogin(), response.getBody().getLogin());
        assertEquals(user.getBirthday(), response.getBody().getBirthday());
    }

    @Test
    void createUser_withInvalidData_returnsBadRequest() {
        User user = new User();
        user.setEmail("invalidemail");
        user.setLogin("");
        user.setBirthday(LocalDate.now().plusYears(1));

        ResponseEntity<Void> response = restTemplate.postForEntity("/users", user, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("invalid_email");
        user.setLogin("test_login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WithEmptyLogin_ShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("test_email@test.com");
        user.setLogin("");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WithSpacesInLogin_ShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("test_email@test.com");
        user.setLogin("test login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WithEmptyName_ShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test_email@test.com");
        user.setLogin("test_login");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getLogin(), response.getBody().getName());
    }

    @Test
    void createUser_WithFutureBirthday_ShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("test_email@test.com");
        user.setLogin("test_login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2025, Month.JANUARY, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}