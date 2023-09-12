package ru.practicum.shareit.user;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class InMemoryUserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) {
        isValid(userDto);
        return userStorage.createUser(userDto);
    }

    @Override
    public User updateUser(Integer userId, User user) {
        User oldUser = userStorage.getUser(userId);
        String userNewName = user.getName();
        String userNewEmail = user.getEmail();
        if (userId == null || userStorage.getUser(userId) == null) {
            throw new NullObjectException("Пользователь с id = " + user.getId() + " не найден!");
        } else if (userId < 0) {
            throw new NegativeValueException("Передано отрицательное значение id!");
        }
        if (userNewName != null) {
            oldUser.setName(userNewName);
        }
        return userStorage.updateUser(oldUser, userNewEmail);
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUser(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NullObjectException("Пользователь с id = " + userId + " не найден!");
        }
        return user;
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
