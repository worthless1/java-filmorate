package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.film.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreMapper;

import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final String filmSql = "SELECT f.*, m.mpa_id, m.name as mpa_name " +
            "FROM films f " +
            "INNER JOIN mpa_ratings m ON f.mpa_id = m.mpa_id ";
//    private final String filmsSql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.genre_name AS genre_name " +
//            "FROM films f " +
//            "INNER JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
//            "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
//            "LEFT JOIN genres g ON fg.genre_id = g.genre_id ";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Films")
                .usingGeneratedKeyColumns("film_id")
                .usingColumns("name", "description", "releaseDate", "duration", "mpa_id");;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("releaseDate", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number filmId = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(filmId.intValue());

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

        // Удаление старых связей фильма с жанрами
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", film.getId());

        if (film.getGenres() != null){
            // Вставка новых связей фильма с жанрами
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        // Обновление основных данных фильма
        jdbcTemplate.update("UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=? WHERE film_id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        return film;
    }

//    @Override
//    public List<Film> getFilms() {
//        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.genre_name AS genre_name " +
//                "FROM films f " +
//                "INNER JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
//                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
//                "LEFT JOIN genres g ON fg.genre_id = g.genre_id ";
//        return jdbcTemplate.query(sql, new FilmMapper());
//    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = jdbcTemplate.query(filmSql, new FilmMapper());
        //add genres to films
        films.forEach(this::addGenresToFilm);

        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = filmSql +
                "WHERE f.film_id=?";

        Film film = jdbcTemplate.query(sql, new FilmMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new FilmDoesntExistException("Film with id: " + id + " does not exists"));

        addGenresToFilm(film);

        return film;
    }

    //likes
    @Override
    public void addLike(int id, int userId) {
        jdbcTemplate.update("INSERT INTO Film_likes (film_id, user_id) VALUES (?, ?)",
                id, userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        jdbcTemplate.update("DELETE FROM Film_likes WHERE film_id=? and user_id=?",
                id, userId);
    }


    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            count = 10; // If count is not set or is invalid, return the first 10 movies
        }

        String sql = "SELECT f.* " +
                "FROM films f " +
                "JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, new FilmMapper(), count);
    }

    private void addGenresToFilm(Film film){
        String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM films f " +
                "LEFT  JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT  JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id=?";

        List<Genre> filmGenres = jdbcTemplate.query(sql, new GenreMapper(), film.getId());

        //checking if film have no genres
        if (filmGenres.contains(Genre.builder().id(0).name(null).build())){
            film.setGenres(new TreeSet<>());
            return;
        }

        film.setGenres(new TreeSet<>(filmGenres));
    }

//    private Film rsToFilm(ResultSet resultSet) throws SQLException {
//        return Film.builder()
//                .id(resultSet.getInt("film_id"))
//                .name(resultSet.getString("name"))
//                .description(resultSet.getString("description"))
//                .releaseDate(resultSet.getDate("release_date").toLocalDate())
//                .duration(resultSet.getInt("duration"))
//                .mpa(Mpa.builder()
//                        .id(resultSet.getInt("MPA_RATINGS.mpa_id"))
//                        .name(resultSet.getString("MPA_RATINGS.name"))
//                        .build())
//                .build();
//    }

}
