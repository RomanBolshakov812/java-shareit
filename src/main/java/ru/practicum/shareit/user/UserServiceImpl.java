package ru.practicum.shareit.user;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        if (userId < 0) {
            throw new NegativeValueException("Передано отрицательное значение id!");
        }
        User udatedUser = repository.findById(userId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + userId + " не найден!"));
        if (userDto.getName() != null) {
            udatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            udatedUser.setEmail(userDto.getEmail());
        }
        repository.save(udatedUser);
        return UserMapper.toUserDto(udatedUser);
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
        return UserMapper.toUserDto(repository.findById(userId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + userId + " не найден!")));
    }
}
