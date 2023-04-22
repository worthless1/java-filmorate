package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class Film {

    private int id;

    @NotBlank(message = "Name must not be empty")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @AssertTrue(message = "Date cannot be earlier than December 28, 1895")
    private boolean isValidDate() {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }

    private LocalDate releaseDate;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be a positive value")
    private int duration;

}
