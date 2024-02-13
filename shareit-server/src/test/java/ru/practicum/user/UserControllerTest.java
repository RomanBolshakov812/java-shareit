package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import ru.practicum.user.dto.UserDto;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private Integer userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        userDto = new UserDto(userId, "Name", "mail@mail.ru");
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto notValidUser = new UserDto(userId, null, null);

        mockMvc.perform(post("/users")
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(notValidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(notValidUser);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsValid_thenReturnedOkStatus() {
        when(userService.addUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDto newUserDto = new UserDto(null, "New Name", null);
        userDto.setName("New Name");
        when(userService.updateUser(userId, newUserDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUser() {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }
}
