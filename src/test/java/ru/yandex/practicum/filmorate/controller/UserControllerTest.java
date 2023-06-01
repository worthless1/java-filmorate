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
import static ru.yandex.practicum.filmorate.model.User.builder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUserWithValidDataReturnsCreatedUser() {
        User user = builder()
                .email("test@example.com")
                .login("testuser")
                .birthday(LocalDate.now().minusYears(20))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getLogin(), response.getBody().getLogin());
        assertEquals(user.getBirthday(), response.getBody().getBirthday());
    }

    @Test
    void createUserWithInvalidDataReturnsBadRequest() {
        User user = builder()
                .email("invalidemail")
                .login("")
                .birthday(LocalDate.now().plusYears(1))
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity("/users", user, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithInvalidEmailShouldReturnBadRequest() {
        User user = builder()
                .email("invalid_email")
                .login("test_login")
                .name("Test Name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithEmptyLoginShouldReturnBadRequest() {
        User user = builder()
                .email("test_email@test.com")
                .login("")
                .name("Test Name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithSpacesInLoginShouldReturnBadRequest() {
        User user = builder()
                .email("test_email@test.com")
                .login("test login")
                .name("Test Name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithEmptyNameShouldUseLoginAsName() {
        User user = builder()
                .email("test_email@test.com")
                .login("test_login")
                .name("")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getLogin(), response.getBody().getName());
    }

    @Test
    void createUserWithFutureBirthdayShouldReturnBadRequest() {
        User user = builder()
                .email("test_email@test.com")
                .login("test_login")
                .name("Test Name")
                .birthday(LocalDate.of(2025, Month.JANUARY, 1))
                .build();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}