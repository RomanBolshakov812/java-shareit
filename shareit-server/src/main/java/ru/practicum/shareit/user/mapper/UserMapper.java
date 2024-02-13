package ru.practicum.shareit.user.mapper;

import java.util.ArrayList;
import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static List<UserDto> toListUserDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }
}
