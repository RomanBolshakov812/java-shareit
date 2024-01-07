package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void BeforeEach() {
        User user1 = userRepository
                .save(new User(null, "Василий", "vasya@mail.ru"));
        User user2 = userRepository
                .save(new User(null, "Габриэлла", "gabi@mail.ru"));
        User user3 = userRepository
                .save(new User(null, "Шандр", "shsh@mail.ru"));
        User user4 = userRepository
                .save(new User(null, "Зинка", "zinka@mail.ru"));
        Item item1 = itemRepository
                .save(new Item(null, "Дрель", "Дрель аккумуляторная",
                        true, user2.getId(), null));
        Item item2 = itemRepository
                .save(new Item(null, "Пила", "Пилит все",
                        true, user2.getId(), null));
        Booking booking1Past = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                item1, user1, BookingState.WAITING));
        Booking booking2Current = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(2),
                item1, user1, BookingState.APPROVED));
        Booking booking3Current = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(3),
                item2, user3, BookingState.APPROVED));
        Booking booking4Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6),
                item2, user3, BookingState.REJECTED));
        Booking booking5Past = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().minusDays(1),
                item2, user4, BookingState.CANCELED));
        Booking booking6Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(8),
                item1, user4, BookingState.APPROVED));
        Booking booking7Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(9),
                item1, user4, BookingState.WAITING));
    }

    @Test
    public void getBookingByIdTest() {
        Optional<Booking> bookingTest = bookingRepository.getBookingById(1);
        assertEquals(1, bookingTest.get().getId());
    }

    // ALL для букера
    @Test
    public  void findBookingByBookerIdOrderByStartDescTest() {
        Pageable page = PageRequest.of(0, 2);
        List<Booking> bookingsByBooker1 = bookingRepository
                .findBookingByBookerIdOrderByStartDesc(1, page).toList();
        assertEquals(2, bookingsByBooker1.size());
        assertEquals(2, bookingsByBooker1.get(0).getId());
        assertEquals(1, bookingsByBooker1.get(1).getId());
        List<Booking> bookingsByBooker3 = bookingRepository
                .findBookingByBookerIdOrderByStartDesc(3, page).toList();
        assertEquals(2, bookingsByBooker3.size());
        assertEquals(4, bookingsByBooker3.get(0).getId());
        assertEquals(3, bookingsByBooker3.get(1).getId());
    }

    // ALL для хозяина вещей
    @Test
    public void findAllBookingByOwnerTest() {
        Pageable page = PageRequest.of(0, 7);
        List<Booking> bookingsByOwner = bookingRepository
                .findAllBookingByOwner(2, page).toList();
        assertEquals(7, bookingsByOwner.size());
        assertEquals(7, bookingsByOwner.get(0).getId());
        assertEquals(5, bookingsByOwner.get(6).getId());
        assertEquals(3, bookingsByOwner.get(3).getId());
    }

    // FUTURE для букера
    @Test
    public void findBookingByBookerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> bookings = bookingRepository
                .findBookingByBookerIdAndStartAfterOrderByStartDesc(3, LocalDateTime.now());
        assertEquals(1, bookings.size());
        assertEquals(4, bookings.get(0).getId());
    }

    // FUTURE для хозяина вещей
    @Test
    public void findAllBookingByOwnerFutureTest() {
        List<Booking> bookings = bookingRepository
                .findAllBookingByOwnerFuture(2);
        assertEquals(3, bookings.size());
        assertEquals(7, bookings.get(0).getId());
        assertEquals(6, bookings.get(1).getId());
        assertEquals(4, bookings.get(2).getId());
    }

    // CURRENT для букера
    @Test
    public void findAllBookingByBookerIdCurrentTest() {
        List<Booking> bookings = bookingRepository
                .findAllBookingByBookerIdCurrent(1);
        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getId());
    }

    // CURRENT для хозяина вещей
    @Test
    public void findAllBookingByOwnerIdCurrentTest() {
        List<Booking> bookings = bookingRepository
                .findAllBookingByOwnerIdCurrent(2);
        assertEquals(2, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(2, bookings.get(1).getId());
    }

    // PAST для букера
    @Test
    public void findAllBookingByBookerPastTest() {
        List<Booking> bookings1 = bookingRepository
                .findAllBookingByBookerPast(1);
        assertEquals(1, bookings1.size());
        assertEquals(1, bookings1.get(0).getId());
        List<Booking> bookings3 = bookingRepository
                .findAllBookingByBookerPast(3);
        assertEquals(0, bookings3.size());
    }

    // PAST для хозяина вещей
    @Test
    public void findAllBookingByOwnerPastTest() {
        List<Booking> bookings1 = bookingRepository
                .findAllBookingByOwnerPast(2);
        assertEquals(2, bookings1.size());
        assertEquals(1, bookings1.get(0).getId());
        assertEquals(5, bookings1.get(1).getId());
    }

    // WAITING, APPROVED, REJECTED, CANCELLED для букера
    @Test
    public void findBookingOfBookerByStatusTest() {
        List<Booking> bookings1Waiting = bookingRepository
                .findBookingOfBookerByStatus(1, BookingState.WAITING);
        assertEquals(1, bookings1Waiting.size());
        assertEquals(1, bookings1Waiting.get(0).getId());

        List<Booking> bookings1Approved = bookingRepository
                .findBookingOfBookerByStatus(1, BookingState.APPROVED);
        assertEquals(1, bookings1Approved.size());
        assertEquals(2, bookings1Approved.get(0).getId());

        List<Booking> bookings3Approved = bookingRepository
                .findBookingOfBookerByStatus(3, BookingState.APPROVED);
        assertEquals(1, bookings3Approved.size());

        List<Booking> bookings3Rejected = bookingRepository
                .findBookingOfBookerByStatus(3, BookingState.REJECTED);
        assertEquals(1, bookings3Rejected.size());
        assertEquals(4, bookings3Rejected.get(0).getId());

        List<Booking> bookings4Waiting = bookingRepository
                .findBookingOfBookerByStatus(4, BookingState.WAITING);
        assertEquals(1, bookings4Waiting.size());
        assertEquals(7, bookings4Waiting.get(0).getId());

        List<Booking> bookings4Cancelled = bookingRepository
                .findBookingOfBookerByStatus(4, BookingState.CANCELED);
        assertEquals(1, bookings4Cancelled.size());
        assertEquals(5, bookings4Cancelled.get(0).getId());
    }

    // WAITING, APPROVED, REJECTED, CANCELLED для хозяина вещей
    @Test
    public void findBookingOfOwnerByStatusTest() {
        List<Booking> bookingsWaiting = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.WAITING);
        assertEquals(2, bookingsWaiting.size());
        assertEquals(7, bookingsWaiting.get(0).getId());
        assertEquals(1, bookingsWaiting.get(1).getId());

        List<Booking> bookingsApproved = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.APPROVED);
        assertEquals(3, bookingsApproved.size());
        assertEquals(6, bookingsApproved.get(0).getId());
        assertEquals(3, bookingsApproved.get(1).getId());
        assertEquals(2, bookingsApproved.get(2).getId());

        List<Booking> bookingsRejected = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.REJECTED);
        assertEquals(1, bookingsRejected.size());
        assertEquals(4, bookingsRejected.get(0).getId());

        List<Booking> bookingsCancelled = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.CANCELED);
        assertEquals(1, bookingsCancelled.size());
        assertEquals(5, bookingsCancelled.get(0).getId());
    }

    // последнее бронирование
    @Test
    public void findTopBookingByItemIdAndStartBeforeOrderByStartDescTest() {
        Booking lastBooking = bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(2, LocalDateTime.now());
        assertEquals(3, lastBooking.getId());
    }

    // следующее бронирование
    @Test
    public void findTopBookingByItemIdAndStartAfterOrderByStartTest() {
        Booking lastBooking = bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(1, LocalDateTime.now());
        assertEquals(6, lastBooking.getId());
    }

    // даты окончаний бронирований букера
    @Test
    public void getListBookingEndDateTest() {
        List<LocalDateTime> endDates = bookingRepository.getListBookingEndDate(1, 1);
        assertEquals(2, endDates.size());
    }

    // Проверка на непересечение броней
    @Test
    public void findOverlapsBookingsTest() {
        // имеется start в запрашиваемом промежутке
        boolean isOverlaidStart = bookingRepository
                .findOverlapsBookings(2, LocalDateTime.now().minusDays(3),
                        LocalDateTime.now());
        // имеется end в запрашиваемом промежутке
        boolean isOverlaidEnd = bookingRepository
                .findOverlapsBookings(1, LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3));
        // запрашиваемый промежуток полностью поглощает имеющееся бронирование
        boolean isOverlaidInside = bookingRepository
                .findOverlapsBookings(1, LocalDateTime.now().plusDays(5),
                        LocalDateTime.now().plusDays(9));
        // пересечение с не APPROVED бронированием
        boolean notOverlaid = bookingRepository
                .findOverlapsBookings(2, LocalDateTime.now().plusDays(4),
                        LocalDateTime.now().plusDays(6));
        assertTrue(isOverlaidStart);
        assertTrue(isOverlaidEnd);
        assertTrue(isOverlaidInside);
        assertFalse(notOverlaid);
    }
}