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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createFilm_shouldReturnBadRequest_whenFilmNameIsEmpty() {
        Film film = new Film();
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(-1);

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilm_shouldReturnBadRequest_whenFilmDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(90);

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilm_shouldReturnBadRequest_whenReleaseDateIsBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1894, 12, 31));
        film.setDuration(90);

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilm_shouldReturnBadRequest_whenDurationIsNegative() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(-1);

        ResponseEntity<Void> response = restTemplate.postForEntity("/films", film, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateFilm_shouldReturnBadRequest_whenFilmNameIsEmpty() {
        Film film = new Film();
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(90);

        ResponseEntity<Void> response = restTemplate.exchange("/films", HttpMethod.PUT, new HttpEntity<>(film), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("This is a test film description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

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