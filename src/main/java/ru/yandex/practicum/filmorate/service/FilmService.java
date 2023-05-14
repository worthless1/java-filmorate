package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.excepton.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        userService.getUser(userId);
        if (film != null) {
            film.getLikes().add(userId);
            filmStorage.updateFilm(film);
        } else {
            throw new FilmDoesntExistException("Film with id: " + id + " does not exist");
        }
    }

    public void removeLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        userService.getUser(userId);
        if (film != null) {
            film.getLikes().remove(userId);
            filmStorage.updateFilm(film);
        } else {
            throw new FilmDoesntExistException("Film with id: " + id + " does not exist");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
