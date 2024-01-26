package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingService bookingService;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Booking booking1;
    private BookingDtoIn bookingDtoIn;
    private List<Booking> bookings;
    private List<BookingDtoOut> expectedBookingsDtoOuts;
    private Page<Booking> bookingPage;
    private Integer from;
    private Integer size;

    @BeforeEach
    public void BeforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository,
                userRepository);
        start = LocalDateTime.now();
        end = start.plusDays(2);
        booker = new User(1, "BookerName", "booker@mail.ru");
        item = new Item(1, "Пила", "Пилит все",
                true, 2, null);
        booking1 = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(2), item,
                booker, null);
        bookingDtoIn = new BookingDtoIn(1, start, end);
        bookings = new ArrayList<>();
        bookings.add(booking1);
        expectedBookingsDtoOuts = BookingMapper.toListBookingDtoOut(bookings);
        bookingPage = new PageImpl<>(bookings);
        from = 0;
        size = 1;

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        lenient().when(userRepository.findById(100))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        lenient().when(itemRepository.findById(100))
                .thenReturn(Optional.empty());
        lenient().when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking1));
    }

    // ADD BOOKING
    @Test
    void addBooking_whenAllOk_thenReturnedBookingWithStatusWaiting() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        UserDto bookerDto = UserMapper.toUserDto(booker);
        when(bookingRepository.findOverlapsBookings(1, start, end))
                .thenReturn(false);
        BookingDtoOut expectedBooking = new BookingDtoOut(null, start, end, itemDto, bookerDto,
                BookingState.WAITING);

        BookingDtoOut actualBooking = bookingService.addBooking(bookingDtoIn, 1);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void addBooking_whenEndBeforeStart_ValidateExceptionThrown() {
        BookingDtoIn bookingWithWrongDates = new BookingDtoIn(1, end, start);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingWithWrongDates, 1));

        assertEquals("Неверные даты бронирования!", exception.getMessage());
    }

    @Test
    void addBooking_whenNotExistingUserId_EntityNullExceptionThrown() {
        EntityNullException exception = assertThrows(EntityNullException.class, () ->
            bookingService.addBooking(bookingDtoIn, 100));

        assertEquals("Пользователь с id = 100 не найден!",
                exception.getMessage());
    }

    @Test
    void addBooking_whenNotExistItemId_EntityNullExceptionThrown() {
        BookingDtoIn bookingWithWrongItemId = new BookingDtoIn(100, start, end);

        EntityNullException exception = assertThrows(EntityNullException.class, () ->
                bookingService.addBooking(bookingWithWrongItemId, 1));

        assertEquals("Вещь с id = 100 не найдена!", exception.getMessage());
    }

    @Test
    void addBooking_whetItemNotAvailable_ValidateExceptionThrown() {
        item.setAvailable(false);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            bookingService.addBooking(bookingDtoIn, 1));

        assertEquals("Данная вещь недоступна!", exception.getMessage());
    }

    @Test
    void addBooking_whenBookerIsOwnerItem_NullObjectExceptionThrown() {
        item.setOwnerId(1);

        NullObjectException exception = assertThrows(NullObjectException.class, () ->
            bookingService.addBooking(bookingDtoIn, 1));

        assertEquals("Хозяин вещи не может ее бронировать! "
                + "Почему - загадка дыры.", exception.getMessage());
    }

    @Test
    void addBooking_whenOverlapsDates_ValidateExceptionThrown() {
        when(bookingRepository.findOverlapsBookings(1, start, end))
                .thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            bookingService.addBooking(bookingDtoIn, 1));

        assertEquals("На указанные даты уже есть бронирование!", exception.getMessage());
    }

    // UPDATE BOOKING
    @Test
    void updateBooking_whenApprovedIsTrue_thenUpdateStateToApproved() {
        booking1.setStatus(BookingState.WAITING);

        BookingDtoOut updateBooking = bookingService
                .updateBooking(1, 2, "true");

        assertEquals(BookingState.APPROVED, updateBooking.getStatus());
    }

    @Test
    void updateBooking_whenApprovedIsFalse_thenUpdateStateToRejected() {
        booking1.setStatus(BookingState.WAITING);

        BookingDtoOut updateBooking = bookingService
                .updateBooking(1, 2, "false");

        assertEquals(BookingState.REJECTED, updateBooking.getStatus());
    }

    @Test
    void updateBooking_whenBookingNotFound_EntityNullExceptionThrown() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        EntityNullException exception = assertThrows(EntityNullException.class, () ->
                bookingService.updateBooking(1, 2, "true"));

        assertEquals("Бронирование с id = 1 не найдено!",
                exception.getMessage());
    }

    @Test
    void updateBooking_whenBookingStateAlreadyApproved_ValidateExceptionThrown() {
        booking1.setStatus(BookingState.APPROVED);

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.updateBooking(1, 2, "true"));

        assertEquals("Бронирование уже подтверждено!", exception.getMessage());
    }

    @Test
    void updateBooking_whenWrongOwnerId_NullObjectExceptionThrown() {
        NullObjectException exception = assertThrows(NullObjectException.class, () ->
                bookingService.updateBooking(1, 100, "true"));

        assertEquals("Пользователь c id = 100 не является хозяином вещи!",
                exception.getMessage());
    }

    // GET BOOKING
    @Test
    void getBooking_whenNotOwnerAndNotBooker_NullObjectExceptionThrown() {
        NullObjectException exception = assertThrows(NullObjectException.class, () ->
            bookingService.getBooking(1, 4));

        assertEquals("Пользователь c id = 4"
                + " не является хозяином вещи или автором бронирования!", exception.getMessage());
    }

    @Test
    void getBooking_whenItemOwnerId_thenReturnedInvoked() {
        BookingDtoOut actualBooking = bookingService.getBooking(1, 2);

        assertEquals(BookingMapper.toBookingDtoOut(booking1), actualBooking);
    }

    @Test
    void getBooking_whenBookerId_thenReturnedInvoked() {
        BookingDtoOut actualBooking = bookingService.getBooking(1, 1);

        assertEquals(BookingMapper.toBookingDtoOut(booking1), actualBooking);
    }

    // GET BOOKING BY BOOKER ID
    @Test
    void getBookingsByBookerId_whenStateFuture_thenReturnedInvoked() {
        when(bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(anyInt(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "FUTURE");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenStateCurrent_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingByBookerCurrent(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "CURRENT");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenStatePast_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingByBookerPast(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "PAST");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenStateWaiting_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfBookerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "WAITING");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenStateApproved_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfBookerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "APPROVED");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenStateRejected_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfBookerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "REJECTED");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenAllState_thenReturnedInvoked() {
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByBookerId(1, from, size, "ALL");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByBookerId_whenBookingsNotFound_thenEntityNullExceptionThrown() {
        bookings.clear();
        bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        EntityNullException exception = assertThrows(EntityNullException.class, () ->
                bookingService.getBookingsByBookerId(1, from, size, "ALL"));

        assertEquals("Бронирований для пользователя с id = 1 не найдено!",
                exception.getMessage());
    }

    // GET BOOKING BY OWNER ID
    @Test
    void getBookingsByOwnerId_whenStateFuture_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingsByOwnerFuture(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "FUTURE");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenStateCurrent_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingByOwnerCurrent(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "CURRENT");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenStatePast_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingByOwnerPast(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "PAST");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenStateWaiting_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfOwnerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "WAITING");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenStateApproved_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfOwnerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "APPROVED");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenStateRejected_thenReturnedInvoked() {
        when(bookingRepository.findBookingOfOwnerByStatus(anyInt(), any(BookingState.class),
                any(Pageable.class))).thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "REJECTED");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenAllState_thenReturnedInvoked() {
        when(bookingRepository.findAllBookingByOwner(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingDtoOut> actualListBookingsDtoOuts = bookingService
                .getBookingsByOwnerId(1, from, size, "ALL");

        assertEquals(expectedBookingsDtoOuts, actualListBookingsDtoOuts);
    }

    @Test
    void getBookingsByOwnerId_whenBookingsNotFound_thenEntityNullExceptionThrown() {
        bookings.clear();
        bookingPage = new PageImpl<>(bookings);
        when(bookingRepository.findAllBookingByOwner(anyInt(), any(Pageable.class)))
                .thenReturn(bookingPage);

        EntityNullException exception = assertThrows(EntityNullException.class, () ->
                bookingService.getBookingsByOwnerId(1, from, size, "ALL"));

        assertEquals("Бронирований для пользователя с id = 1 не найдено!",
                exception.getMessage());
    }
}