package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
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

    Set<Integer> friends = new HashSet<>();


    // Getter for "name" field that returns "login" value if "name" is empty
    public String getName() {
        return name != null && !name.isEmpty() ? name : login;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("Email", email);
        values.put("Login", login);
        values.put("Name", name);
        values.put("Birthday", birthday);
        return values;
    }

}
