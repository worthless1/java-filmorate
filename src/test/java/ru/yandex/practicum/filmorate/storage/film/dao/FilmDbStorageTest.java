package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO Films (Name, Description, Releasedate, Duration, Mpa_id) VALUES (?, ?, ?, ?, ?)",
                "name", "description", LocalDate.of(2015, 11, 11), 111, 1);

        jdbcTemplate.update("INSERT INTO Films (Name, Description, Releasedate, Duration, Mpa_id) VALUES (?, ?, ?, ?, ?)",
                "name2", "description2", LocalDate.of(2015, 12, 12), 222, 2);
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM Films");

        jdbcTemplate.update("ALTER TABLE Films ALTER COLUMN Film_id RESTART WITH 1");
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("movie3")
                .description("testCreateMovie")
                .releaseDate(LocalDate.of(2013, 3, 3))
                .duration(333)
                .mpa(Mpa.builder().id(3).build())
                .build();

        filmStorage.createFilm(film);

        Film testFilm = filmStorage.getFilmById(3);

        assertEquals(3, testFilm.getId());
        assertEquals("movie3", testFilm.getName());
        assertEquals("testCreateMovie", testFilm.getDescription());
        assertEquals(LocalDate.of(2013, 3, 3), testFilm.getReleaseDate());
        assertEquals(333, testFilm.getDuration());
        assertEquals(3, testFilm.getMpa().getId());

    }

    @Test
    void updateFilm() {
        Film film = Film.builder()
                .id(1)
                .name("filmUpdate")
                .description("testUpdateFilm")
                .releaseDate(LocalDate.of(2022, 2, 2))
                .duration(333)
                .mpa(Mpa.builder().id(3).build())
                .build();

        filmStorage.updateFilm(film);

        Film testFilm = filmStorage.getFilmById(1);

        assertEquals("filmUpdate", testFilm.getName());
        assertEquals("testUpdateFilm", testFilm.getDescription());
        assertEquals(LocalDate.of(2022, 2, 2), testFilm.getReleaseDate());
        assertEquals(333, testFilm.getDuration());
        assertEquals(3, testFilm.getMpa().getId());
    }

    @Test
    void getFilms() {
        List<Film> userList = filmStorage.getFilms();

        assertEquals(2, userList.size());
    }

    @Test
    void getFilmById() {
        int id = 1;
        Film testFilm = filmStorage.getFilmById(id);

        assertEquals(1, testFilm.getId());
        assertEquals("name", testFilm.getName());
        assertEquals("description", testFilm.getDescription());
        assertEquals(LocalDate.of(2015, 11, 11), testFilm.getReleaseDate());
        assertEquals(111, testFilm.getDuration());
        assertEquals(1, testFilm.getMpa().getId());
    }

}