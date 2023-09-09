package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    public User addUser(User user);

    public User updateUser(Integer userId, User user);

    public void deleteUser(Integer userId);

    public List<User> getAllUsers();

    public User getUser(Integer id);
}
