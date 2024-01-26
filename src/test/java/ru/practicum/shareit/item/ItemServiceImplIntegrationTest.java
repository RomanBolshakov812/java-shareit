package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private UserDto userDto;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    public void BeforeEach() {
        userDto = new UserDto(null, "User", "mail@mail.ru");
        itemDto1 = new ItemDto(null, "item1", "d1", true,
                null, null, null, null);
        itemDto2 = new ItemDto(null, "item2", "d2", true,
                null, null, null, null);
        userDto = userService.addUser(userDto);
        itemDto1 = itemService.addItem(itemDto1, userDto.getId());
        itemDto2 = itemService.addItem(itemDto2, userDto.getId());
    }

    @Test
    void getItemsByOwnerId() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i "
                + "where i.ownerId = :id", Item.class);
        query.setParameter("id", userDto.getId());
        List<Item> queryResultList = query.getResultList();
        List<ItemDto> expectedItemsList = ItemMapper.toListItemDto(queryResultList);

        List<ItemDto> actualItemsList = itemService.getItemsByOwnerId(userDto.getId());

        assertThat(actualItemsList.size(), equalTo(2));
        assertThat(expectedItemsList, equalTo(actualItemsList));
        assertThat(actualItemsList.get(0), equalTo(itemDto1));
        assertThat(actualItemsList.get(1), equalTo(itemDto2));
    }

    @AfterEach
    public void deleteAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}