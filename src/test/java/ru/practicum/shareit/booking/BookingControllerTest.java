package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private Integer userId;
    private Integer bookingId;
    private BookingDtoIn bookingDtoIn;
    private BookingDtoOut bookingDtoOut;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        bookingId = 1;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        UserDto userDto = new UserDto(1, "Name", "mail@mail.ru");
        ItemDto itemDto = new ItemDto(1, "Name", "Description", true,
                null, null, null, null);
        bookingDtoIn = new BookingDtoIn(1, start, end);
        bookingDtoOut = new BookingDtoOut(1, start, end, itemDto, userDto, BookingState.WAITING);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingNotValid_thenReturnedBadRequest() {
        BookingDtoIn notValidBooking = new BookingDtoIn();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(notValidBooking)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(notValidBooking, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingValid_thenReturnedOkStatus() {
        when(bookingService.addBooking(bookingDtoIn, userId)).thenReturn(bookingDtoOut);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoOut), result);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        bookingDtoOut.setStatus(BookingState.APPROVED);
        when(bookingService.updateBooking(bookingId, userId, "true"))
                .thenReturn(bookingDtoOut);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoOut), result);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getBooking(bookingId, userId);
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

        verify(bookingService, never())
                .getBookingsByBookerId(userId, -1, 1, "WAITING");
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

        verify(bookingService, never())
                .getBookingsByBookerId(userId, 1, 0, "WAITING");
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

        verify(bookingService, never())
                .getBookingsByBookerId(userId, 1, 21, "WAITING");
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

        verify(bookingService).getBookingsByBookerId(userId, 0, 1, "WAITING");
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenNegativeFrom_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "-1")
                        .param("size", "1")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingService, never())
                .getBookingsByOwnerId(userId, -1, 1, "WAITING");
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenSizeLessOne_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "0")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingService, never())
                .getBookingsByOwnerId(userId, 1, 0, "WAITING");
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerId_whenSizeMore20_thenReturnedBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "21")
                        .param("state", "WAITING"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(bookingService, never())
                .getBookingsByOwnerId(userId, 1, 21, "WAITING");
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

        verify(bookingService).getBookingsByOwnerId(userId, 0, 1, "WAITING");
    }
}
