package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.request.dto.RequestDto;

@WebMvcTest(RequestControllerGateway.class)
class RequestControllerGatewayTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestClient requestClient;
    private Integer requestId;
    private Integer userId;
    private ResponseEntity<Object> response;

    @BeforeEach
    void beforeEach() {
        requestId = 1;
        userId = 1;
    }

    @SneakyThrows
    @Test
    void createRequest_whenRequestIsValid_thenReturnedOkStatus() {
        LocalDateTime created = LocalDateTime.now();
        RequestDto requestDto = new RequestDto(null, "description", created);
        ResponseEntity<Object> response = new ResponseEntity<>(requestDto, HttpStatus.OK);
        when(requestClient.createRequest(requestDto, userId)).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestClient).createRequest(requestDto, userId);
    }

    @SneakyThrows
    @Test
    void getRequest() {
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(requestClient).getRequest(requestId, userId);
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

        verify(requestClient).getRequestsByRequestorId(userId, 0, 1);
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

        verify(requestClient).getRequestsByOtherUsers(userId, 0, 1);
    }
}
