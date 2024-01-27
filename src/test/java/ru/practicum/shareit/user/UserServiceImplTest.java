package ru.practicum.shareit.user;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NegativeValueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User(1, "Name", "mail@mail.ru");
        when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(100)).thenReturn(Optional.empty());
        lenient().when(userRepository.save(user)).thenReturn(user);
    }

    @Test
    void addUser() {
        UserDto userDtoIn = new UserDto(1, "Name", "mail@mail.ru");
        UserDto expectedUser = UserMapper.toUserDto(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUser = userService.addUser(userDtoIn);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUser_whenNegativeUserId_NegativeValueExceptionThrown() {
        UserDto actualUser = UserMapper.toUserDto(user);

        assertThrows(NegativeValueException.class,
                () -> userService.updateUser(-1, actualUser));
    }

    @Test
    void updateUser_whenUserNotFound_EntityNullExceptionThrown() {
        UserDto actualUser = UserMapper.toUserDto(user);

        assertThrows(EntityNullException.class,
                () -> userService.updateUser(100, actualUser));
    }

    @Test
    void updateUser_whenUpdateName_thenReturnedUser() {
        UserDto userWithNewName = new UserDto(null, "New Name", null);
        User expectedUser = new User(1, "New Name", "mail@mail.ru");

        UserDto actualUser = userService.updateUser(1, userWithNewName);

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
    }

    @Test
    void updateUser_whenUpdateEmail_thenReturnedUser() {
        UserDto userWithNewEmail = new UserDto(null, null, "newEmail@mail.ru");
        User expectedUser = new User(1, "Name", "newEmail@mail.ru");

        UserDto actualUser = userService.updateUser(1, userWithNewEmail);

        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void getAllUsers() {
        userService.getAllUsers();

        verify(userRepository).findAll();
    }

    @Test
    void getUser_whenFoundUser_thenReturnedUser() {
        UserDto actualUser = userService.getUser(1);

        assertEquals(UserMapper.toUserDto(user), actualUser);
    }

    @Test
    void getUser_whenUserNotFound_thenEntityNullExceptionThrown() {
        assertThrows(EntityNullException.class, () -> userService.getUser(100));
    }
}
