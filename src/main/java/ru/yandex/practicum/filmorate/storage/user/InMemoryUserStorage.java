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
    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user != null && friend != null) {

            //if the user has already received a friend request
            if (user.getFriends().get(userId) != null) {
                user.getFriends().put(friendId, true);
                friend.getFriends().put(userId, true);
                updateUser(user);
                updateUser(friend);
            } else {
                friend.getFriends().put(userId, false);
                //update friend list
                updateUser(friend);
            }

        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    //friends
    @Override
    public void removeFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            //update friend list
            updateUser(user);
            updateUser(friend);
        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    @Override
    public List<User> getFriends(int id) {
        User user = getUserById(id);
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getFriends().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(entry -> getUserById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> friends = getFriends(id);
        List<User> otherFriends = getFriends(otherId);

        return friends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }

}
