package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.user.model.User;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void BeforeEach() {
        User user1 = userRepository
                .save(new User(null, "Василий", "vasya@mail.ru"));
        User user2 = userRepository
                .save(new User(null, "Габриэлла", "gabi@mail.ru"));
    }

    @Test
    public void getUserByIdTest() {
        Optional<User> user = userRepository.getUserById(1);
        assertEquals(1, user.get().getId());
    }
}