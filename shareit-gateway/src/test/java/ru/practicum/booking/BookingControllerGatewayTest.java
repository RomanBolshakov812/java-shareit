package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.exception.UnsupportedStatusException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

@WebMvcTest(BookingControllerGateway.class)
class BookingControllerGatewayTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;
    private Integer userId;
    private Integer bookingId;
    private BookingDto bookingDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        bookingId = 1;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        UserDto userDto = new UserDto(1, "Name", "mail@mail.ru");
        ItemDto itemDto = new ItemDto(1, "Name", "Description", true,
                null, null, null, null);
        bookingDto = new BookingDto(1, start, end);
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingValid_thenReturnedOkStatus() {
        when(bookingClient.createBooking(bookingDto, userId)).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient).createBooking(bookingDto, userId);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        when(bookingClient.updateBooking(bookingId, userId, "true"))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient).updateBooking(bookingId, userId, "true");
    }

    @SneakyThrows
    @Test
    void getBooking() {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_whenUnsupportedStatus_thenReturnedBadRequest() throws Exception {
        String exceptionParam = "Unknown state: UNSUPPORTED_STATUS";
        mockMvc.perform(get("/bookings", exceptionParam)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "UNSUPPORTED_STATUS"))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UnsupportedStatusException))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_whenNegativeFrom_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .param("size", "1")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByBookerId(userId, -1, 1, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_whenSizeLessOne_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "0")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByBookerId(userId, 1, 0, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_whenSizeMore20_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "21")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByBookerId(userId, 1, 21, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerId_whenAllOk_thenReturnedOkStatus() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByBookerId(userId, 0, 1, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenUnsupportedStatus_thenReturnedBadRequest() throws Exception {
        String exceptionParam = "Unknown state: UNSUPPORTED_STATUS";
        mockMvc.perform(get("/bookings/owner", exceptionParam)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "UNSUPPORTED_STATUS"))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UnsupportedStatusException))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenNegativeFrom_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .param("size", "1")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByOwnerId(userId, -1, 1, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenSizeLessOne_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "0")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByOwnerId(userId, 1, 0, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenSizeMore20_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "21")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .getBookingsByOwnerId(userId, 1, 21, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenAllOk_thenReturnedOkStatus() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByOwnerId(userId, 0, 1, BookingState.WAITING);
    }
}
