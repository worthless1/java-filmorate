package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excepton.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.FilmDoesntExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("films")
@Validated
@Slf4j
public class FilmController {

    private final List<Film> films = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        if (films.contains(film)) {
            throw new FilmAlreadyExistException("This film already exists");
        }

        int id = films.size() + 1; // Increment the size of the films list to set as the id
        film.setId(id); // Set the id of the created film

        films.add(film);
        log.debug("Film added: {}", film.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.remove(film)) {
            throw new FilmDoesntExistException("This film does not exists");
        }

        films.add(film);
        log.debug("Film updated: {}", film.toString());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return films;
    }

}