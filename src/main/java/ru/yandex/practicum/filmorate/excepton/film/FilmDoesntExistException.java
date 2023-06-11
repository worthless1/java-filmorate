package ru.yandex.practicum.filmorate.excepton.film;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class FilmDoesntExistException extends FilmorateException {

    public FilmDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }

}
