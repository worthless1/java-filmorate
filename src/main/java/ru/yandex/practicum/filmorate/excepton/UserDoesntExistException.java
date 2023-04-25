package ru.yandex.practicum.filmorate.excepton;

public class UserDoesntExistException extends RuntimeException {
    public UserDoesntExistException(String message) {
        super(message);
    }

}
