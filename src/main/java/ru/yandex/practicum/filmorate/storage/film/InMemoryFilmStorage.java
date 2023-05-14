package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

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

}
