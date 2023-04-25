package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class User {

    int id;

    @Email(message = "Email must be valid")
    String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "^\\S*$", message = "Name field cannot contain spacess")
    String login;

    String name;

    @PastOrPresent(message = "Birthday cannot be in the future")
    LocalDate birthday;

    // Getter for "name" field that returns "login" value if "name" is empty
    public String getName() {
        return name != null && !name.isEmpty() ? name : login;
    }

}
