package ru.practicum.shareit.user;

import java.util.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int generatedId = 1;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new DuplicateException("Пользователь с таким email уже существует!");
        }
        userDto.setId(generatedId++);
        User user = UserMapper.toUser(userDto);
        emails.add(userDto.getEmail());
        users.put(user.getId(), user);
        return userDto;
    }

    @Override
    public User updateUser(User user, String userNewEmail) {
        if (userNewEmail != null && !user.getEmail().equals(userNewEmail)) {
            if (emails.contains(userNewEmail)) {
                throw new DuplicateException("Пользователь с таким email уже существует!");
            }
            emails.remove(user.getEmail());
            emails.add(userNewEmail);
            user.setEmail(userNewEmail);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }
}
