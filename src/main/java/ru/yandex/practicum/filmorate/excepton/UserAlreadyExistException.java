package ru.yandex.practicum.filmorate.excepton;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends FilmorateException {

    public UserAlreadyExistException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
