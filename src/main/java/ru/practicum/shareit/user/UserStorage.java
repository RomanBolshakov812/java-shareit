package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    public User createUser(User user);

    public User updateUser(User user);

    public void deleteUser(Integer userId);

    public List<User> getAllUsers();

    public User getUser(Integer id);
}
