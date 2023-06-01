package ru.yandex.practicum.filmorate.excepton.mpa;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class MpaDoesntExistException extends FilmorateException {

    public MpaDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }

}
