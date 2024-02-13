package ru.practicum.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.BookingState;
import ru.practicum.booking.dto.BookingDtoShort;
import ru.practicum.booking.model.Booking;
import ru.practicum.request.RequestRepository;
import ru.practicum.exception.EntityNullException;
import ru.practicum.exception.NullObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RequestRepository requestRepository;
    private ItemService itemService;
    private User user;
    private Item item;
    private ItemDto emptyItem;
    private ItemDto itemDto;
    private Booking lastBooking;
    private Booking nextBooking;
    private BookingDtoShort lastBookingDtoShort;
    private BookingDtoShort nextBookingDtoShort;
    private Integer wrongUserId;
    private Integer wrongItemId;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository,
                commentRepository, userRepository, bookingRepository, requestRepository);
        wrongUserId = 100;
        wrongItemId = 100;
        item = new Item(1, "item1", "item1 description",
                true, 1, null);
        user = new User(1, "Василий", "vasya@mail.ru");
        emptyItem = new ItemDto(null, null, null, null, null,
                null, null, null);
        itemDto = new ItemDto(1, "item1", "item1 description", true,
                null, null, null, null);
        User booker = new User(2, "Петр", "petya@mail.ru");
        lastBooking = new Booking(1, LocalDateTime.parse("2023-01-01T01:01:01"),
                LocalDateTime.now().plusDays(1), item, booker, BookingState.APPROVED);
        nextBooking = new Booking(2, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingState.APPROVED);
        lastBookingDtoShort = new BookingDtoShort(1, 2);
        nextBookingDtoShort = new BookingDtoShort(2, 2);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        lenient().when(itemRepository.findById(wrongItemId)).thenReturn(Optional.empty());
        lenient().when(itemRepository.save(item)).thenReturn(item);
    }

    // ADD ITEM
    @Test
    void addItem_whenUserNotFound_thenEntityNullExceptionThrown() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> itemService.addItem(itemDto, wrongUserId));

        assertEquals("Пользователь с id = 100 не найден!", exception.getMessage());
    }

    @Test
    void addItem_withRequest_thenReturnedItemWithRequest() {
        User requestor = new User(2, "Петр", "petya@mail.ru");
        Request request = new Request(1, "description", requestor,
                LocalDateTime.now(), null);
        ItemDto itemDtoWithRequest = new ItemDto(1,
                "item1", "item1 description", true,
                null, null, null, 1);
        Item itemToSaveInRepository = ItemMapper.toItem(itemDtoWithRequest, 1, request);
        Item expectedItem = new Item(1, "item1", "item1 description",
                true, 1, request);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.save(itemToSaveInRepository)).thenReturn(expectedItem);
        ItemDto expectedItemDto = ItemMapper.toItemDto(expectedItem);

        ItemDto actualItemDto = itemService.addItem(itemDtoWithRequest, 1);

        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void addItem_whenNonRequest_thenReturnedItemWithoutRequest() {
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.addItem(itemDto, 1);

        assertEquals(expectedItemDto, actualItemDto);
    }

    // UPDATE ITEM
    @Test
    void updateItem_whenItemNotFound_thenEntityNullExceptionThrown() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> itemService.updateItem(wrongItemId, itemDto, 1));

        assertEquals("Вещь с id = 100 не найдена!", exception.getMessage());
    }

    @Test
    void updateItem_whenUserNotFound_thenNullObjectExceptionThrown() {
        NullObjectException exception = assertThrows(NullObjectException.class,
                () -> itemService.updateItem(1, itemDto, 2));

        assertEquals("Неверный id владельца вещи!", exception.getMessage());
    }

    @Test
    void updateItem_whenUpdateName_thenReturnedItemWithNewName() {
        itemDto = emptyItem;
        itemDto.setName("New Name");
        Item expectedItem = new Item(1, "New Name", "item1 description",
                true, 1, null);

        ItemDto actualItemDto = itemService.updateItem(1, itemDto, 1);

        assertEquals(ItemMapper.toItemDto(expectedItem), actualItemDto);
    }

    @Test
    void updateItem_whenUpdateDescription_thenReturnedItemWithNewName() {
        itemDto = emptyItem;
        itemDto.setDescription("New description");
        Item expectedItem = new Item(1, "item1", "New description",
                true, 1, null);

        ItemDto actualItemDto = itemService.updateItem(1, itemDto, 1);

        assertEquals(ItemMapper.toItemDto(expectedItem), actualItemDto);
    }

    @Test
    void updateItem_whenUpdateAvailable_thenReturnedItemWithNewName() {
        itemDto = emptyItem;
        itemDto.setAvailable(false);
        Item expectedItem = new Item(1, "item1", "item1 description",
                false, 1, null);

        ItemDto actualItemDto = itemService.updateItem(1, itemDto, 1);

        assertEquals(ItemMapper.toItemDto(expectedItem), actualItemDto);
    }

    //GET ITEM
    @Test
    void getItemsByOwnerId() {
        itemService.getItemsByOwnerId(1);

        verify(itemRepository).findAllItemsByOwnerIdOrderById(1);
    }

    @Test
    void getItem_whenItemNotFound_thenReturnedEntityNullException() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> itemService.getItem(wrongItemId, 1));

        assertEquals("Вещь с id = 100 не найдена!", exception.getMessage());
    }

    @Test
    void getItem_whenNonNextAndLastBooking_thenReturnedInvoked() {
        when(commentRepository.findAllCommentByItemId(anyInt())).thenReturn(List.of());
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(List.of());

        ItemDto actualItemDto = itemService.getItem(1, 1);

        assertEquals(expectedItemDto, actualItemDto);
    }

    //SEARCH ITEM
    @Test
    void searchItem_whenTextIsBlank_thenReturnedEmptyList() {
        List<ItemDto> expectedList = new ArrayList<>();

        List<ItemDto> actualList = itemService.searchItem("");

        assertEquals(expectedList, actualList);
    }

    @Test
    void searchItem_whenTextIsNotBlank_thenReturnedItemsList() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemDto> expectedList = new ArrayList<>();
        expectedList.add(ItemMapper.toItemDto(item));
        when(itemRepository
                .findByDescriptionContainingIgnoreCaseAndAvailable(any(String.class),
                        anyBoolean())).thenReturn(items);

        List<ItemDto> actualList = itemService.searchItem("item1 de");

        assertEquals(expectedList, actualList);
    }

    // ADD COMMENT
    @Test
    void addComment_whenItemNotFound_thenEntityNulExceptionThrown() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> itemService.addComment(null, wrongItemId, 1));

        assertEquals("Вещь с id = 100 не найдена!", exception.getMessage());
    }

    @Test
    void addComment_whenUserNotFound_thenEntityNulExceptionThrown() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> itemService.addComment(null, 1, wrongUserId));

        assertEquals("Пользователь с id = 100 не найден!", exception.getMessage());
    }

    @Test
    void addComment_whenNoBookingsForItemForItem_thenValidationExceptionThrown() {
        when(bookingRepository.getListBookingEndDate(anyInt(), anyInt())).thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(null, 1, 1));

        assertEquals("У пользователя с id = 1 нет бронирований вещи с id = 1!",
                exception.getMessage());
    }

    @Test
    void addComment_whenNoCompletedBookings_thenValidationExceptionThrown() {
        List<LocalDateTime> endDateBookings = new ArrayList<>();
        endDateBookings.add(LocalDateTime.now().plusDays(1));
        when(bookingRepository.getListBookingEndDate(anyInt(), anyInt()))
                .thenReturn(endDateBookings);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(null, 1, 1));

        assertEquals("У пользователя с id = 1 нет завершенных бронирований вещи с id = 1!",
                exception.getMessage());
    }

    @Test
    void addComment_whenOneBookingAvailable_thenReturnedInvoked() {
        LocalDateTime created = LocalDateTime.now();
        List<LocalDateTime> endDateBookings = new ArrayList<>();
        endDateBookings.add(LocalDateTime.now());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(bookingRepository.getListBookingEndDate(anyInt(), anyInt()))
                .thenReturn(endDateBookings);
        CommentDto commentDtoIn = new CommentDto(null, "Text", itemDto,
                "Author Name", null);
        Comment comment = new Comment(null, "Text", item, user, created);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto expectedCommentDto = CommentMapper.toCommentDto(comment);

        CommentDto actualCommentDto = itemService.addComment(commentDtoIn,
                item.getId(), user.getId());
        actualCommentDto.setCreated(created);

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    // GET LAST BOOKING
    @Test
    void getLastBooking_whenLastBookingNotFound_thenReturnedNull() {
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(anyInt(),
                        any(LocalDateTime.class))).thenReturn(null);
        lenient().when(commentRepository.findAllCommentByItemId(anyInt()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualLastBooking = actualItemDto.getLastBooking();

        assertNull(actualLastBooking);
    }

    @Test
    void getLastBooking_whenLastBookingIsRejected_thenReturnedNull() {
        lastBooking.setStatus(BookingState.REJECTED);
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(item.getId(),
                        LocalDateTime.now())).thenReturn(lastBooking);
        lenient().when(commentRepository.findAllCommentByItemId(anyInt()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualLastBooking = actualItemDto.getLastBooking();

        assertNull(actualLastBooking);
    }

    @Test
    void getLastBooking_whenLastBookingIsFound_thenReturnedLastBooking() {
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(anyInt(),
                        any(LocalDateTime.class))).thenReturn(lastBooking);
        lenient().when(commentRepository.findAllCommentByItemId(anyInt()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualLastBooking = actualItemDto.getLastBooking();

        assertEquals(lastBookingDtoShort, actualLastBooking);
    }

    // GET NEXT BOOKING
    @Test
    void getNextBooking_whenNextBookingNotFound_thenReturnedNull() {
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(anyInt(),
                        any(LocalDateTime.class))).thenReturn(null);
        lenient().when(commentRepository.findAllCommentByItemId(item.getId()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualNextBooking = actualItemDto.getNextBooking();

        assertNull(actualNextBooking);
    }

    @Test
    void getNextBooking_whenNextBookingIsRejected_thenReturnedNull() {
        nextBooking.setStatus(BookingState.REJECTED);
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(item.getId(),
                        LocalDateTime.now())).thenReturn(nextBooking);
        lenient().when(commentRepository.findAllCommentByItemId(item.getId()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualNextBooking = actualItemDto.getNextBooking();

        assertNull(actualNextBooking);
    }

    @Test
    void getNextBooking_whenNextBookingIsFound_thenReturnedNextBooking() {
        lenient().when(bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(anyInt(),
                        any(LocalDateTime.class))).thenReturn(nextBooking);
        lenient().when(commentRepository.findAllCommentByItemId(anyInt()))
                .thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getItem(item.getId(), user.getId());
        BookingDtoShort actualNextBooking = actualItemDto.getNextBooking();

        assertEquals(nextBookingDtoShort, actualNextBooking);
    }
}
