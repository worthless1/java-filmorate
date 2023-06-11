package ru.yandex.practicum.filmorate.excepton.user;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

public class UserAlreadyExistException extends FilmorateException {

    public UserAlreadyExistException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
