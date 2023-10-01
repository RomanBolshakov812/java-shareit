package ru.practicum.shareit.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> getUserById(Integer userId);
}
