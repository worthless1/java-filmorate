package ru.yandex.practicum.filmorate.excepton.user;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class UserDoesntExistException extends FilmorateException {

    public UserDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }

}
