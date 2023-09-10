package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    public User addUser(User user);

    public User updateUser(Integer userId, User user);

    public void deleteUser(Integer userId);

    public List<User> getAllUsers();

    public User getUser(Integer id);
}
