package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilmById(int id);

    //likes
    void addLike(int id, int userId);

    void removeLike(int id, int userId);

    //returns a list of the first count of movies by number of likes.
    // If count is not set, return the first 10.
    List<Film> getPopularFilms(int count);

}
