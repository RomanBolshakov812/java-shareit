package ru.practicum.shareit.user;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class InMemoryUserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) {
        isValid(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User oldUser = userStorage.getUser(userId);
        if (userId == null || oldUser == null) {
            throw new NullObjectException("Пользователь с id = " + userId + " не найден!");
        } else if (userId < 0) {
            throw new NegativeValueException("Передано отрицательное значение id!");
        }
        User updateUser = UserMapper.toUser(userDto);
        String userNewName = updateUser.getName();
        String userNewEmail = updateUser.getEmail();
        if (userNewName == null) {
            updateUser.setName(oldUser.getName());
        }
        if (userNewEmail == null) {
            updateUser.setEmail(oldUser.getEmail());
        }
        updateUser.setId(userId);
        return UserMapper.toUserDto(userStorage.updateUser(updateUser));
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListUserDto(userStorage.getAllUsers());
    }

    @Override
    public UserDto getUser(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NullObjectException("Пользователь с id = " + userId + " не найден!");
        }
        return UserMapper.toUserDto(user);
    }

    private  void isValid(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Отсутствуют данные пользователя!");
        } else if (userDto.getEmail() == null || userDto.getEmail().isBlank()
                || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Неверная электронная почта!");
        } else if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Неверное имя пользователя!");
        }
    }
}
