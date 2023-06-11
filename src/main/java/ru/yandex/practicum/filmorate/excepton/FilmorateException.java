package ru.yandex.practicum.filmorate.excepton;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@RequiredArgsConstructor
@Builder
public class FilmorateException extends RuntimeException {

    private final String errorMessage;
    private final HttpStatus httpStatus;

}
