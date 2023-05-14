package ru.yandex.practicum.filmorate.excepton;

import org.springframework.http.HttpStatus;

public class UserDoesntExistException extends FilmorateException {

    public UserDoesntExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }

}
