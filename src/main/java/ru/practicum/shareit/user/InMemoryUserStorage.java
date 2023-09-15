package ru.practicum.shareit.user;

import java.util.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int generatedId = 1;

    @Override
    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new DuplicateException("Пользователь с таким email уже существует!");
        }
        user.setId(generatedId++);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        User oldUser = users.get(updateUser.getId());
        String userOldEmail = oldUser.getEmail();
        String userNewEmail = updateUser.getEmail();
        if (!userNewEmail.equals(userOldEmail)) {
            if (emails.contains(userNewEmail)) {
                throw new DuplicateException("Пользователь с таким email уже существует!");
            }
            emails.remove(userOldEmail);
            emails.add(userNewEmail);
        }
        users.put(updateUser.getId(), updateUser);
        return updateUser;
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
