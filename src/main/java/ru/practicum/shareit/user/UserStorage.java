package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    public UserDto createUser(UserDto userDto);

    public User updateUser(User user, String userEmail);

    public void deleteUser(Integer userId);

    public List<User> getAllUsers();

    public User getUser(Integer id);
}
