package ru.practicum.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import ru.practicum.booking.dto.BookingDtoIn;
import ru.practicum.booking.dto.BookingDtoOut;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

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
