package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, Integer bookerId) {
        isValid(bookingDtoIn);
        User booker = userRepository.getUserById(bookerId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + bookerId + " не найден!"));
        Item item = itemRepository.getItemById(bookingDtoIn.getItemId()).orElseThrow(() ->
                new EntityNullException("Вещь с id = " + bookerId + " не найдена!"));
        if (bookerId.equals(item.getOwnerId())) {
            throw new NullObjectException("Хозяин вещи не может ее бронировать! "
                    + "Почему - загадка дыры.");
        }
        Booking booking = BookingMapper.toBookingOfDtoIn(bookingDtoIn, item, booker);
        booking.setStatus(BookingState.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut updateBooking(Integer bookingId, Integer userId, String approved) {
        BookingState state;
        if (Objects.equals(approved, "true")) {
            state = BookingState.APPROVED;
        } else {
            state = BookingState.REJECTED;
        }
        Booking booking = bookingRepository.getById(bookingId);
        Item item = booking.getItem();
        if (Objects.equals(item.getOwnerId(), userId)) {
            if (!booking.getStatus().equals(BookingState.APPROVED)) {
                booking.setStatus(state);
            } else {
                throw new ValidationException("Бронирование уже подтверждено!");
            }
            return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
        }
        throw new NullObjectException("Пользователь c id = " + userId
                + " не является хозяином вещи!");
    }

    @Override
    public BookingDtoOut getBooking(Integer bookingId, Integer sharerId) {
        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new EntityNullException("Бронирование с id = "
                        + bookingId + " не найдено!"));
        if (!Objects.equals(booking.getBooker().getId(), sharerId)
                & !Objects.equals(booking.getItem().getOwnerId(), sharerId)) {
            throw new NullObjectException("Пользователь c id = "
                    + sharerId + " не является хозяином вещи или автором бронирования!");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getBookingsByBookerId(Integer bookerId, String state) {
        List<Booking> bookings;
        switch (state) {
            case "FUTURE":
                bookings = bookingRepository
                        .findBookingByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                                LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllBookingByBookerIdCurrent(bookerId);
                break;
            case "PAST":
                bookings = bookingRepository.findAllBookingByBookerPast(bookerId);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingOfBookerByStatus(bookerId,
                        BookingState.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingOfBookerByStatus(bookerId,
                        BookingState.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository
                        .findBookingByBookerIdOrderByStartDesc(bookerId);
                break;
            default:
                throw new UnsupportedStatusException("В запросе передан неверный статус: " + state);
        }
        if (bookings.size() == 0) {
            throw new EntityNullException("Бронирований для пользователя с id = "
                    + bookerId + " не найдено!");
        }
        return BookingMapper.toListBookingDtoOut(bookings);
    }

    @Override
    public List<BookingDtoOut> getBookingsByOwnerId(Integer ownerId, String state) {
        List<Booking> bookings;
        switch (state) {
            case "FUTURE":
                bookings = bookingRepository.findAllBookingByOwnerFuture(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllBookingByOwnerIdCurrent(ownerId);
                break;
            case "PAST":
                bookings = bookingRepository.findAllBookingByOwnerPast(ownerId);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingOfOwnerByStatus(ownerId,
                        BookingState.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingOfOwnerByStatus(ownerId,
                        BookingState.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findAllBookingByOwner(ownerId);
                break;
            default:
                throw new UnsupportedStatusException("В запросе передан неверный статус: " + state);
        }
        if (bookings.size() == 0) {
            throw new EntityNullException("Бронирований для пользователя с id = "
                    + ownerId + " не найдено!");
        }
        return BookingMapper.toListBookingDtoOut(bookings);
    }

    private  void isValid(BookingDtoIn bookingDtoIn) {
        LocalDateTime start = bookingDtoIn.getStart();
        LocalDateTime end = bookingDtoIn.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("Неверные даты бронирования!");
        }

        try {
            Item item = itemRepository.getById(bookingDtoIn.getItemId());
            if (!item.getAvailable()) {
                throw new ValidationException("Данная вещь недоступна!");
            } else if (start.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Неверная дата начала бронирования!");
            } else if (end.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Неверная дата окончания бронирования!");
            } else if (end.isBefore(start) || start.equals(end)) {
                throw new ValidationException("Неверные даты бронирования!");
            }
        } catch (EntityNotFoundException e) {
            throw new EntityNullException("Отсутствует id вещи!");
        }
    }
}
