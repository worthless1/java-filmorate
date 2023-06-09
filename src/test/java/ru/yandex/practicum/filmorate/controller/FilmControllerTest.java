package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createFilmShouldReturnBadRequestWhenFilmNameIsEmpty() {
        Film film = Film.builder()
                .description("Test Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(-1)
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmShouldReturnBadRequestWhenFilmDescriptionIsTooLong() {
        Film film = Film.builder()
                .name("Test Film")
                .description("A".repeat(201))
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(90)
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmShouldReturnBadRequestWhenReleaseDateIsBefore1895() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(1894, 12, 31))
                .duration(90)
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmShouldReturnBadRequestWhenDurationIsNegative() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(-1)
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateFilmShouldReturnBadRequestWhenFilmNameIsEmpty() {
        Film film = Film.builder()
                .description("Test Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(90)
                .build();

        ResponseEntity<Void> response = restTemplate.exchange("/films", HttpMethod.PUT, new HttpEntity<>(film), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateFilmWithValidData() {
        Film film = Film.builder()
                .name("Test Film")
                .description("This is a test film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(Mpa.builder().id(1).build())
                .build();

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());
        Film createdFilm = response.getBody();

        assertEquals("Test Film", createdFilm.getName());
        assertEquals("This is a test film description", createdFilm.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());
    }

}