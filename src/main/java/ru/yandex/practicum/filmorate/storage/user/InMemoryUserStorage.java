package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("This user already exists");
        }

        int id = users.size() + 1; // Increment the size of the users list to set as the id
        user.setId(id); // Set the id of the created user
        users.put(id, user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new UserDoesntExistException("This user does not exists");
        }

        users.put(id, user);

        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);

        if (user != null) {
            return user;
        } else {
            throw new UserDoesntExistException("This user does not exists");
        }
    }

}
