package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@AllArgsConstructor
public class InMemoryUserService implements UserService {

    private UserStorage userStorage;
    private final Map<String, Integer> emails = new HashMap<>();

    @Override
    public User addUser(User user) {
        isValid(user);
        if (isNotUniqueEmail(user.getEmail())) {
            throw new DuplicateException("Пользователь с таким email уже существует!");
        }
        emails.put(user.getEmail(), user.getId());
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(Integer userId, User user) {
        User oldUser = userStorage.getUser(userId);
        User updatedUser = new User();
        String userNewName = user.getName();
        String userNewEmail = user.getEmail();
        boolean isNewEmail = false;
        if (userId == null || userStorage.getUser(userId) == null) {
            throw new NullObjectException("Пользователь с id = " + user.getId() + " не найден!");
        } else if (userId < 0) {
            throw new NegativeValueException("Передано отрицательное значение id!");
        }
        updatedUser.setId(userId);
        if (userNewEmail != null) {
            if (isNotUniqueEmail(userNewEmail)) {
                if (!emails.get(userNewEmail).equals(userId)) {
                    throw new DuplicateException("Пользователь с таким email уже существует!");
                }
            } else {
                isNewEmail = true;
            }
            updatedUser.setEmail(userNewEmail);
        } else {
            updatedUser.setEmail(oldUser.getEmail());
        }
        if (userNewName != null) {
            updatedUser.setName(userNewName);
        } else {
            updatedUser.setName(oldUser.getName());
        }
        isValid(updatedUser);
        if (isNewEmail) {
            emails.remove(oldUser.getEmail());
        }
        return userStorage.updateUser(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        emails.remove(userStorage.getUser(userId).getEmail());
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

    private Boolean isNotUniqueEmail(String email) {
        return emails.containsKey(email);
    }

    private  void isValid(User user) {
        if (user == null) {
            throw new ValidationException("Отсутствуют данные пользователя!");
        } else if (user.getEmail() == null || user.getEmail().isBlank() ||
                !user.getEmail().contains("@")) {
            throw new ValidationException("Неверная электронная почта!");
        } else if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Неверное имя пользователя!");
        }
    }
}
