package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excepton.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
@Validated
@Slf4j
public class UserController {

    private final List<User> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            throw new UserAlreadyExistException("This user already exists");
        }

        int id = users.size() + 1; // Increment the size of the users list to set as the id
        user.setId(id); // Set the id of the created user

        users.add(user);
        log.debug("User added: {}", user.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        if (!users.remove(user)) {
            throw new UserDoesntExistException("This user does not exists");
        }

        users.add(user);
        log.debug("User updated: {}", user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

}
