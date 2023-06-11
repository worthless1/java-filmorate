package ru.yandex.practicum.filmorate.excepton.film;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class FilmAlreadyExistException extends FilmorateException {

    public FilmAlreadyExistException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
