package ru.yandex.practicum.filmorate.excepton;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String message) {
        super(message);
    }

}
