package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.film.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreMapper;

import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
    private static final String FILM_SQL = "SELECT f.*, m.mpa_id, m.name as mpa_name " +
            "FROM films f " +
            "INNER JOIN mpa_ratings m ON f.mpa_id = m.mpa_id ";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Films")
                .usingGeneratedKeyColumns("film_id")
                .usingColumns("name", "description", "releaseDate", "duration", "mpa_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("releaseDate", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number filmId = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(filmId.intValue());

        if (film.getGenres() != null) {
            // add genres
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        //checking if the film exists
        List<Integer> filmIds = jdbcTemplate.queryForList(
                "SELECT film_id FROM films WHERE film_id=?", Integer.class, film.getId());

        if (filmIds.isEmpty()) {
            throw new FilmDoesntExistException("Film with id: " + film.getId() + " does not exist");
        }

        // removing old genres from a movie
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", film.getId());

        if (film.getGenres() != null) {
            // inserting new genres into the film
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        // updating basic movie data
        jdbcTemplate.update("UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE film_id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = jdbcTemplate.query(FILM_SQL, new FilmMapper());
        //add genres to films
        films.forEach(this::addGenresToFilm);

        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = FILM_SQL +
                "WHERE f.film_id=?";

        Film film = jdbcTemplate.query(sql, new FilmMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new FilmDoesntExistException("Film with id: " + id + " does not exists"));

        addGenresToFilm(film);

        return film;
    }

    //likes
    @Override
    public void addLike(int filmId, int userId) {
        //check that the film and the user exist
        getFilmById(filmId);
        userService.getUser(userId);

        jdbcTemplate.update("INSERT INTO Film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        //check that the film and the user exist
        getFilmById(filmId);
        userService.getUser(userId);

        jdbcTemplate.update("DELETE FROM Film_likes WHERE film_id=? and user_id=?", filmId, userId);
    }


    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            count = 10; // If count is not set or is invalid, return the first 10 movies
        }

        String sql = FILM_SQL +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), count);
        //add genres to films
        films.forEach(this::addGenresToFilm);
        return films;
    }

    private void addGenresToFilm(Film film) {
        String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM films f " +
                "LEFT  JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT  JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id=?";

        List<Genre> filmGenres = jdbcTemplate.query(sql, new GenreMapper(), film.getId());

        //checking if film have no genres
        if (filmGenres.contains(Genre.builder().id(0).name(null).build())) {
            film.setGenres(new TreeSet<>());
            return;
        }

        film.setGenres(new TreeSet<>(filmGenres));
    }

}
