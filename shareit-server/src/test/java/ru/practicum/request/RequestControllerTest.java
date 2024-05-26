package ru.practicum.request;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;
    private Integer requestId;
    private Integer userId;

    @BeforeEach
    void beforeEach() {
        requestId = 1;
        userId = 1;
    }

    @SneakyThrows
    @Test
    void createRequest_whenRequestIsValid_thenReturnedOkStatus() {
        LocalDateTime created = LocalDateTime.now();
        RequestDtoIn requestDtoIn = new RequestDtoIn(null, "description", created);
        RequestDtoOut requestDtoOut = new RequestDtoOut(1, "description",
                created, null);
        when(requestService.addRequest(requestDtoIn, userId)).thenReturn(requestDtoOut);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(requestDtoIn)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestDtoOut), result);
    }

    @SneakyThrows
    @Test
    void getRequest() {
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).getRequest(requestId, userId);
    }

    @SneakyThrows
    @Test
    void getRequestByRequestorId() {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).getRequestsByRequestorId(userId, 0, 1);
    }

    @SneakyThrows
    @Test
    void getRequestsByOtherUsers() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestService).getRequestsOtherUsers(userId, 0, 1);
    }
}
