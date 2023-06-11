package ru.yandex.practicum.filmorate.controller.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.excepton.FilmorateException;

import java.util.Map;

@ControllerAdvice
public class FilmorateErrorHandler {

    @ExceptionHandler
    protected ResponseEntity<Object> handleFilmorateException(final FilmorateException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(Map.of("error message ", ex.getErrorMessage()));
    }

}
