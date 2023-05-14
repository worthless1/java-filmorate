package ru.yandex.practicum.filmorate.excepton;

import org.springframework.http.HttpStatus;

public class FilmDoesntExistException extends FilmorateException {

    public FilmDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }

}
