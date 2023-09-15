package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    public UserDto addUser(UserDto userDto);

    public UserDto updateUser(Integer userId, UserDto userDto);

    public void deleteUser(Integer userId);

    public List<UserDto> getAllUsers();

    public UserDto getUser(Integer id);
}
