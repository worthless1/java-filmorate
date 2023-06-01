package ru.yandex.practicum.filmorate.excepton.genre;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class GenreDoesntExistException extends FilmorateException {
    public GenreDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}
