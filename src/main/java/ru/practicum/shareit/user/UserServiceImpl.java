package ru.practicum.shareit.user;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto userDto) {
        isValid(userDto);
        User user = UserMapper.toUser(userDto);
        repository.save(user);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {

        User oldUser = repository.getById(userId);
        if (userId < 0) {
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
        return UserMapper.toUserDto(repository.save(updateUser));
    }

    @Override
    public void deleteUser(Integer userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListUserDto(repository.findAll());
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(repository.getUserById(userId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + userId + " не найден!")));
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
