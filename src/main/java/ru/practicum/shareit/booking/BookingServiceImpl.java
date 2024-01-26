package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Integer itemId = bookingDtoIn.getItemId();
        LocalDateTime start = bookingDtoIn.getStart();
        LocalDateTime end = bookingDtoIn.getEnd();
        if (end.isBefore(start) || start.equals(end)) {
            throw new ValidationException("Неверные даты бронирования!");
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + bookerId + " не найден!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNullException("Вещь с id = " + itemId + " не найдена!"));
        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь недоступна!");
        }
        if (bookerId.equals(item.getOwnerId())) {
            throw new NullObjectException("Хозяин вещи не может ее бронировать! "
                    + "Почему - загадка дыры.");
        } else if (bookingRepository.findOverlapsBookings(itemId, start, end)) {
            throw new ValidationException("На указанные даты уже есть бронирование!");
        }
        Booking booking = BookingMapper.toBookingOfDtoIn(bookingDtoIn, item, booker);
        booking.setStatus(BookingState.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut updateBooking(Integer bookingId, Integer ownerId, String approved) {
        BookingState state;
        if (Objects.equals(approved, "true")) {
            state = BookingState.APPROVED;
        } else {
            state = BookingState.REJECTED;
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNullException("Бронирование с id = " + bookingId + " не найдено!"));
        Item item = booking.getItem();
        if (Objects.equals(item.getOwnerId(), ownerId)) {
            if (!booking.getStatus().equals(BookingState.APPROVED)) {
                booking.setStatus(state);
            } else {
                throw new ValidationException("Бронирование уже подтверждено!");
            }
            bookingRepository.save(booking);
            return BookingMapper.toBookingDtoOut(booking);
        }
        throw new NullObjectException("Пользователь c id = " + ownerId
                + " не является хозяином вещи!");
    }

    @Override
    public BookingDtoOut getBooking(Integer bookingId, Integer sharerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNullException("Бронирование с id = "
                        + bookingId + " не найдено!"));
        if (!Objects.equals(booking.getBooker().getId(), sharerId)) {
            if (!Objects.equals(booking.getItem().getOwnerId(), sharerId)) {
                throw new NullObjectException("Пользователь c id = "
                        + sharerId + " не является хозяином вещи или автором бронирования!");
            }
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getBookingsByBookerId(Integer bookerId, Integer from,
                                                     Integer size, String state) {
        List<Booking> bookings;
        Pageable page = bookingsToPage(from, size);
        switch (stateToEnum(state)) {
            case FUTURE:
                bookings = bookingRepository
                        .findBookingByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                                LocalDateTime.now(), page).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllBookingByBookerCurrent(bookerId, page).toList();
                break;
            case PAST:
                bookings = bookingRepository.findAllBookingByBookerPast(bookerId, page).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findBookingOfBookerByStatus(bookerId,
                        BookingState.WAITING, page).toList();
                break;
            case APPROVED:
                bookings = bookingRepository.findBookingOfBookerByStatus(bookerId,
                        BookingState.APPROVED, page).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingOfBookerByStatus(bookerId,
                        BookingState.REJECTED, page).toList();
                break;
            default:
                bookings = bookingRepository
                        .findBookingByBookerIdOrderByStartDesc(bookerId, page).toList();
                break;
        }
        if (bookings.size() == 0) {
            throw new EntityNullException("Бронирований для пользователя с id = "
                    + bookerId + " не найдено!");
        }
        return BookingMapper.toListBookingDtoOut(bookings);
    }

    @Override
    public List<BookingDtoOut> getBookingsByOwnerId(Integer ownerId, Integer from,
                                                    Integer size, String state)
            throws UnsupportedStatusException {
        List<Booking> bookings;
        Pageable page = bookingsToPage(from, size);
        switch (stateToEnum(state)) {
            case FUTURE:
                bookings = bookingRepository.findAllBookingsByOwnerFuture(ownerId, page).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllBookingByOwnerCurrent(ownerId, page).toList();
                break;
            case PAST:
                bookings = bookingRepository.findAllBookingByOwnerPast(ownerId, page).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findBookingOfOwnerByStatus(ownerId,
                        BookingState.WAITING, page).toList();
                break;
            case APPROVED:
                bookings = bookingRepository.findBookingOfOwnerByStatus(ownerId,
                        BookingState.APPROVED, page).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingOfOwnerByStatus(ownerId,
                        BookingState.REJECTED, page).toList();
                break;
            default:
                bookings = bookingRepository.findAllBookingByOwner(ownerId, page).toList();
                break;
        }
        if (bookings.size() == 0) {
            throw new EntityNullException("Бронирований для пользователя с id = "
                    + ownerId + " не найдено!");
        }
        return BookingMapper.toListBookingDtoOut(bookings);
    }

    private  Pageable bookingsToPage(Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        int startPage = from / size;
        return PageRequest.of(startPage, size, sort);
    }

    private BookingState stateToEnum(String state) {
        BookingState status;
        try {
            status = BookingState.valueOf(state);
            return status;
        } catch (RuntimeException e) {
            throw new UnsupportedStatusException("Передан неверный статус бронирования: "
                    + state + "!");
        }
    }
}
