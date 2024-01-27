package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private BookingDtoIn bookingDtoIn1;
    private BookingDtoIn bookingDtoIn2;
    private BookingDtoOut bookingDtoOut1;
    private BookingDtoOut bookingDtoOut2;
    private Integer from;
    private Integer size;

    @BeforeEach
    public void beforeEach() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        userDto1 = new UserDto(null, "User1", "mail1@mail.ru");
        userDto2 = new UserDto(null, "User2", "mail2@mail.ru");
        itemDto1 = new ItemDto(null, "item1", "d1", true,
                null, null, null, null);
        itemDto2 = new ItemDto(null, "item2", "d2", true,
                null, null, null, null);
        bookingDtoIn1 = new BookingDtoIn(1, start, end.plusDays(5));
        bookingDtoIn2 = new BookingDtoIn(2, start.plusDays(1), end.plusDays(7));
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        itemDto1 = itemService.addItem(itemDto1, 2);
        itemDto2 = itemService.addItem(itemDto2, 2);
        bookingDtoOut1 = bookingService.addBooking(bookingDtoIn1, 1);
        bookingDtoOut2 = bookingService.addBooking(bookingDtoIn2, 1);
    }

    @Test
    void getBookingsByBookerId() {
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b "
                + "where b.booker.id = :id order by b.start desc", Booking.class);
        query.setParameter("id", userDto1.getId());
        List<Booking> queryResultList = query.getResultList();
        from = 0;
        size = 2;
        List<BookingDtoOut> expectedBookingsList = BookingMapper
                .toListBookingDtoOut(queryResultList);

        List<BookingDtoOut> actualBookingsList =
                bookingService.getBookingsByBookerId(1, from, size, "WAITING");

        assertThat(actualBookingsList.size(), equalTo(2));
        assertThat(expectedBookingsList, equalTo(actualBookingsList));
        assertThat(actualBookingsList.get(0), equalTo(bookingDtoOut2));
        assertThat(actualBookingsList.get(1), equalTo(bookingDtoOut1));
    }

    @AfterEach
    public void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
