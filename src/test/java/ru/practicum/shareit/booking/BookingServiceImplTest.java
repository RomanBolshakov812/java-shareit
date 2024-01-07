package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@SpringJUnitConfig
class BookingServiceImplTest {

    @Mock
    BookingRepository BookingRepository;
    @Mock
    UserRepository UserRepository;
    @Mock
    ItemRepository ItemRepository;
    @Mock
    BookingServiceImpl bookingService;
    LocalDateTime start;
    LocalDateTime end;
    User booker;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    public void BeforeEach() {
        bookingService = new BookingServiceImpl(BookingRepository, ItemRepository,
                UserRepository);
        start = LocalDateTime.now();
        end = start.plusDays(2);
        booker = new User(1, "Букер", "booker@mail.ru");
        owner = new User(2, "Владелец", "owner@mail.ru");
        item = new Item(1, "Пила", "Пилит все",
                true, 2, null);
        booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item,
                booker, BookingState.WAITING);
        when(ItemRepository.getItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        when(BookingRepository.getBookingById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
    }

    //Должен вернуть бронирование со статусом WAITING
    @Test
    void addBooking() {
        when(UserRepository.getUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(booker));
        when(BookingRepository.findOverlapsBookings(1, start, end))
                .thenReturn(false);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(1, start, end);
        BookingDtoOut newBooking = bookingService.addBooking(bookingDtoIn, 1);
        assertEquals(start, newBooking.getStart());
        assertEquals(end, newBooking.getEnd());
        assertEquals(ItemMapper.toItemDto(item), newBooking.getItem());
        assertEquals(UserMapper.toUserDto(booker), newBooking.getBooker());
        assertEquals(BookingState.WAITING, newBooking.getStatus());
    }

    // Должен обновить статус бронирования с WAITING на APPROVED
    @Test
    void shouldUpdateStateBooking() {
        when(BookingRepository.getBookingById(1))
                .thenReturn(Optional.of(booking));
        BookingDtoOut updateBooking = bookingService
                .updateBooking(1, 2, "true");
        assertEquals(BookingState.APPROVED, updateBooking.getStatus());
    }

    // Запрос бронирования с неверным id пользователя
    @Test
    void getBookingWitsWrongIdUser() {
        NullObjectException exception = Assertions.assertThrows(NullObjectException.class, () -> {
            bookingService.getBooking(1, 4);
        });
        Assertions.assertEquals("Пользователь c id = 4"
                + " не является хозяином вещи или автором бронирования!", exception.getMessage());
    }
}