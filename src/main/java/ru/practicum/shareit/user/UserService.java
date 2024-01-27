package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);

    List<UserDto> getAllUsers();

    UserDto getUser(Integer id);
}
