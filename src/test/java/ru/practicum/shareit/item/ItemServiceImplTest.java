package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    ItemDto itemDto;



    @BeforeEach
    public void BeforeEach() {
        itemService = new ItemServiceImpl(itemRepository,
                commentRepository, userRepository, bookingRepository);
        Item item = new Item(1, "item1", "item1 description",
                true, 1, null);
        User user = new User(1, "Василий", "vasya@mail.ru");
        itemDto = new ItemDto(null,
                "item1", "item1 description", true,
                null, null, null, null);
        when(userRepository.getUserById(1)).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(ItemMapper.toItem(itemDto, 1));
    }

    @Test
    public void addItemTest() {
        ItemDto itemDtoTest = itemService.addItem(this.itemDto, 1);
        assertEquals(this.itemDto, itemDtoTest);
    }
}