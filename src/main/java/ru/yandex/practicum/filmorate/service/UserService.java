package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.excepton.UserDoesntExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.createUser(user);
    }

    public User update(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
            //update friend list
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    public void removeFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
            //update friend list
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } else {
            throw new UserDoesntExistException("User or friend with given id does not exist");
        }
    }

    public List<User> getFriends(int id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> commonFriends = new HashSet<>(userStorage.getUserById(id).getFriends());
        commonFriends.retainAll(userStorage.getUserById(otherId).getFriends());
        return commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

}
