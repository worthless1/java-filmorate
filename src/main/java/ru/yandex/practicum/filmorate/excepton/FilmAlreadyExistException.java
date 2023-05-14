package ru.yandex.practicum.filmorate.excepton;

import org.springframework.http.HttpStatus;

public class FilmAlreadyExistException extends FilmorateException {

    public FilmAlreadyExistException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
