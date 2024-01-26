package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final UserRepository userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    public  void BeforeEach() {
        user1 = new User(null, "User1", "mail1@mail.ru");
        user2 = new User(null, "User2", "mail2@mail.ru");
    }

    @Test
    void addUser() {
        UserDto userDto = UserMapper.toUserDto(user1);
        userService.addUser(userDto);

        TypedQuery<User> query =
                em.createQuery("Select u from User u where u.name = :name", User.class);
        User actualUser = query.setParameter("name", user1.getName()).getSingleResult();

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo(user1.getName()));
        assertThat(actualUser.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void getAllUsers() {
        UserDto userDto1 = UserMapper.toUserDto(user1);
        UserDto userDto2 = UserMapper.toUserDto(user2);
        userService.addUser(userDto1);
        userService.addUser(userDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> actualUsersList = query.getResultList();

        User actualUser1 = actualUsersList.get(0);
        User actualUser2 = actualUsersList.get(1);
        assertThat(actualUsersList.size(), equalTo(2));
        assertThat(actualUser1.getId(), notNullValue());
        assertThat(actualUser2.getId(), notNullValue());
        assertThat(actualUser1.getName(), equalTo("User1"));
        assertThat(actualUser2.getName(), equalTo("User2"));
        assertThat(actualUser1.getEmail(), equalTo("mail1@mail.ru"));
        assertThat(actualUser2.getEmail(), equalTo("mail2@mail.ru"));
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
    }
}