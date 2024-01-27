package ru.practicum.shareit.booking;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
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
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Item item1;
    private Item item2;
    private Booking booking1Past;
    private Booking booking2Past;
    private Booking booking3Current;
    private Booking booking4Current;
    private Booking booking5Future;
    private Booking booking6Future;
    private Booking booking7Future;

    @BeforeEach
    public void beforeEach() {
        user1 = userRepository
                .save(new User(null, "Василий", "vasya@mail.ru"));
        user2 = userRepository
                .save(new User(null, "Габриэлла", "gabi@mail.ru"));
        user3 = userRepository
                .save(new User(null, "Шандр", "shsh@mail.ru"));
        user4 = userRepository
                .save(new User(null, "Зинка", "zinka@mail.ru"));
        item1 = itemRepository
                .save(new Item(null, "Дрель", "Дрель аккумуляторная",
                        true, user2.getId(), null));
        item2 = itemRepository
                .save(new Item(null, "Пила", "Пилит все",
                        true, user2.getId(), null));
        booking1Past = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().minusDays(1),
                item2, user4, BookingState.CANCELED));
        booking2Past = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                item1, user1, BookingState.WAITING));
        booking3Current = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(2),
                item1, user1, BookingState.APPROVED));
        booking4Current = bookingRepository.save(new Booking(null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(3),
                item2, user3, BookingState.APPROVED));
        booking5Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6),
                item2, user3, BookingState.REJECTED));
        booking6Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusDays(8),
                item1, user4, BookingState.APPROVED));
        booking7Future = bookingRepository.save(new Booking(null,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(9),
                item1, user4, BookingState.WAITING));
    }

    // ALL для букера
    @Test
    public  void findBookingByBookerIdOrderByStartDescTest() {
        List<Booking> expectedBookings
                = new ArrayList<>(Arrays.asList(booking3Current, booking2Past));

        Pageable page = PageRequest.of(0, 2);
        List<Booking> actualBookings = bookingRepository
                .findBookingByBookerIdOrderByStartDesc(1, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // ALL для хозяина вещей
    @Test
    public void findAllBookingByOwnerTest() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking7Future,
                booking6Future,
                booking5Future,
                booking4Current,
                booking3Current,
                booking2Past,
                booking1Past));
        Pageable page = PageRequest.of(0, 7);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingByOwner(2, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // FUTURE для букера
    @Test
    public void findAllBookingsByBookerFutureTest() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking7Future,
                booking6Future));
        Pageable page = PageRequest.of(0, 2);

        List<Booking> actualBookings = bookingRepository
                .findBookingByBookerIdAndStartAfterOrderByStartDesc(4,
                        LocalDateTime.now(), page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // FUTURE для хозяина вещей
    @Test
    public void findAllBookingsByOwnerFutureTest() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking7Future,
                booking6Future,
                booking5Future));
        Pageable page = PageRequest.of(0, 3);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingsByOwnerFuture(2, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // CURRENT для букера
    @Test
    public void findAllBookingByBookerIdCurrentTest() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking4Current));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingByBookerCurrent(3, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // CURRENT для хозяина вещей
    @Test
    public void findAllBookingByOwnerIdCurrentTest() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking4Current,
                booking3Current));
        Pageable page = PageRequest.of(0, 2);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingByOwnerCurrent(2, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // PAST для букера
    @Test
    public void findAllBookingByBookerPastTest() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking2Past));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingByBookerPast(1, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // PAST для хозяина вещей
    @Test
    public void findAllBookingByOwnerPastTest() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking2Past,
                booking1Past));
        Pageable page = PageRequest.of(0, 2);

        List<Booking> actualBookings = bookingRepository
                .findAllBookingByOwnerPast(2, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // WAITING, APPROVED, REJECTED, CANCELLED для букера
    @Test
    public void findBookingOfBookerByStatus_whenWaitingForUser1_thenReturnedBooking2() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking2Past));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfBookerByStatus(1, BookingState.WAITING, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfBookerByStatus_whenApprovedForUser3_thenReturnedBooking4() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking4Current));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfBookerByStatus(3, BookingState.APPROVED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfBookerByStatus_whenRejectedForUser3_thenReturnedBooking5() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking5Future));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfBookerByStatus(3, BookingState.REJECTED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfBookerByStatus_whenCancelledForUser4_thenReturnedBooking1() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking1Past));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfBookerByStatus(4, BookingState.CANCELED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // WAITING, APPROVED, REJECTED, CANCELLED для хозяина вещей
    @Test
    public void findBookingOfOwnerByStatus_whenWaitingForUser2_thenReturnedBookings7And2() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking7Future,
                booking2Past));
        Pageable page = PageRequest.of(0, 2);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.WAITING, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfOwnerByStatus_whenApprovedForUser2_thenReturnedBooking6And4And3() {
        List<Booking> expectedBookings = new ArrayList<>(Arrays.asList(
                booking6Future,
                booking4Current,
                booking3Current));
        Pageable page = PageRequest.of(0, 3);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.APPROVED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfOwnerByStatus_whenRejectedForUser2_thenReturnedBooking5() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking5Future));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.REJECTED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    public void findBookingOfOwnerByStatus_whenCancelledForUser2_thenReturnedBooking1() {
        List<Booking> expectedBookings = new ArrayList<>(List.of(booking1Past));
        Pageable page = PageRequest.of(0, 1);

        List<Booking> actualBookings = bookingRepository
                .findBookingOfOwnerByStatus(2, BookingState.CANCELED, page).toList();

        assertEquals(expectedBookings, actualBookings);
    }

    // последнее бронирование
    @Test
    public void findTopBookingByItemIdAndStartBeforeOrderByStartDescTest() {
        Booking actualLastBooking = bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(2, LocalDateTime.now());

        assertEquals(booking4Current, actualLastBooking);
    }

    // следующее бронирование
    @Test
    public void findTopBookingByItemIdAndStartAfterOrderByStartTest() {
        Booking actualLastBooking = bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(1, LocalDateTime.now());

        assertEquals(booking6Future, actualLastBooking);
    }

    // даты окончаний бронирований букера
    @Test
    public void getListBookingEndDateTest() {
        bookingRepository.delete(booking2Past);
        bookingRepository.delete(booking3Current);
        LocalDateTime currentDateTame =
                LocalDateTime.parse("2124-04-01T01:01:01");
        Booking booking8 = new Booking(null, currentDateTame.minusDays(2),
                currentDateTame.plusDays(1), item1, user1, BookingState.APPROVED);
        Booking booking9 = new Booking(null, currentDateTame.minusDays(1),
                currentDateTame.plusDays(2), item1, user1, BookingState.APPROVED);
        bookingRepository.save(booking8);
        bookingRepository.save(booking9);
        List<LocalDateTime> expectedDatesList = new ArrayList<>(Arrays.asList(
                booking8.getEnd(),
                booking9.getEnd()));

        List<LocalDateTime> actualDatesList = bookingRepository
                .getListBookingEndDate(1, 1);

        assertEquals(expectedDatesList, actualDatesList);
    }

    // НЕПЕРЕСЕЧЕНИЕ БРОНЕЙ
    // start попадает в другое бронирование
    @Test
    public void findOverlapsBookings_whenStartFallsInAnotherDiapason_thenReturnedTrue() {
        boolean startIsOverlaid = bookingRepository
                .findOverlapsBookings(2, LocalDateTime.now().minusDays(3),
                        LocalDateTime.now());

        assertTrue(startIsOverlaid);
    }

    // end попадает в другое бронирование
    @Test
    public void findOverlapsBookings_EndFallsInAnotherBooking_thenReturnedTrue() {
        boolean endIsOverlaid = bookingRepository
                .findOverlapsBookings(1, LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7));

        assertTrue(endIsOverlaid);
    }

    // запрашиваемое бронирование полностью перекрывает другое бронирование
    @Test
    public void findOverlapsBookings_BookingFullyOverlapsAnotherBooking_thenReturnedTrue() {
        boolean isOverlaidOutside = bookingRepository
                .findOverlapsBookings(2, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(4));

        assertTrue(isOverlaidOutside);
    }

    // пересечение с REJECTED бронированием
    @Test
    public void findOverlapsBookings_BookingOverlapsWithRejectedBooking_thenReturnedFalse() {
        boolean notOverlaid = bookingRepository
                .findOverlapsBookings(2, LocalDateTime.now().plusDays(4),
                        LocalDateTime.now().plusDays(6));

        assertFalse(notOverlaid);
    }

    @AfterEach
    public void deleteBooking() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
