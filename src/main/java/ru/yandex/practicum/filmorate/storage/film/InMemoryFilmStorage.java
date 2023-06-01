package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.film.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.film.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final UserService userService;

    @Autowired
    public InMemoryFilmStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("This film already exists");
        }

        int id = films.size() + 1; // Increment the size of the films list to set as the id
        film.setId(id); // Set the id of the created film
        films.put(id, film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new FilmDoesntExistException("This film does not exists");
        }
        films.put(id, film);
        return films.get(id);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);

        if (film != null) {
            return film;
        } else {
            throw new FilmDoesntExistException("This film does not exists");
        }
    }

    @Override
    public void  addLike(int id, int userId) {
        Film film = getFilmById(id);
        userService.getUser(userId);
        if (film != null) {
            film.getLikes().add(userId);
            updateFilm(film);
        } else {
            throw new FilmDoesntExistException("Film with id: " + id + " does not exist");
        }
    }

    @Override
    public void removeLike(int id, int userId) {
        Film film = getFilmById(id);
        userService.getUser(userId);
        if (film != null) {
            film.getLikes().remove(userId);
            updateFilm(film);
        } else {
            throw new FilmDoesntExistException("Film with id: " + id + " does not exist");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
