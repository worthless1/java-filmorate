package ru.yandex.practicum.filmorate.excepton;

public class FilmDoesntExistException extends RuntimeException {
    public FilmDoesntExistException(String message) {
        super(message);
    }

}
