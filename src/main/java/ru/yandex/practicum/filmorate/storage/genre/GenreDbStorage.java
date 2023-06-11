package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.genre.GenreDoesntExistException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM Genres", new GenreMapper());
    }


    public Genre getGenreById(int id) {
        return jdbcTemplate.query("SELECT * FROM Genres WHERE Genre_id=?", new GenreMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new GenreDoesntExistException("Genre with id: " + id + " does not exists"));
    }

}
