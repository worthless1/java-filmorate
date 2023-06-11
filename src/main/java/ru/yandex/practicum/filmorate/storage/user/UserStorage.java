package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(int id);

    //friends
    void addFriend(int id, int friendId);

    void removeFriend(int id, int friendId);

    List<User> getFriends(int id);

    public List<User> getCommonFriends(int id, int otherId);

}
