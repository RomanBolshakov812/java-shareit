package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.user.dto.UserDto;

@WebMvcTest(UserControllerGateway.class)
class UserControllerGatewayTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;
    private Integer userId;
    private UserDto userDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        userDto = new UserDto(userId, "Name", "mail@mail.ru");
        response = new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsValid_thenReturnedOkStatus() {
        when(userClient.createUser(userDto)).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userClient).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        when(userClient.updateUser(userId, userDto)).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userClient).updateUser(userId, userDto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUser() {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(userId);
    }
}
