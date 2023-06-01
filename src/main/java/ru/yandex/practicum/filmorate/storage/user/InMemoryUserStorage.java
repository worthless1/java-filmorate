package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.user.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.excepton.user.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void addFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
            //update friend list
            updateUser(user);
            updateUser(friend);
        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    //friends
    @Override
    public void removeFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
            //update friend list
            updateUser(user);
            updateUser(friend);
        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    @Override
    public List<User> getFriends(int id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> commonFriends = new HashSet<>(getUserById(id).getFriends());
        commonFriends.retainAll(getUserById(otherId).getFriends());
        return commonFriends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

}
