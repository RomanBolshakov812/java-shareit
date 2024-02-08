package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private User user;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    public void beforeEach() {
        user = new User(null, "User", "mail@mail.ru");
        itemDto1 = new ItemDto(null, "item1", "d1", true,
                null, null, null, null);
        itemDto2 = new ItemDto(null, "item2", "d2", true,
                null, null, null, null);
        em.persist(user);
        itemDto1 = itemService.addItem(itemDto1, user.getId());
        itemDto2 = itemService.addItem(itemDto2, user.getId());
    }

    @Test
    void getItemsByOwnerId() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i "
                + "where i.ownerId = :id", Item.class);
        query.setParameter("id", user.getId());
        List<Item> queryResultList = query.getResultList();
        List<ItemDto> expectedItemsList = ItemMapper.toListItemDto(queryResultList);

        List<ItemDto> actualItemsList = itemService.getItemsByOwnerId(user.getId());

        assertThat(actualItemsList.size(), equalTo(2));
        assertThat(expectedItemsList, equalTo(actualItemsList));
        assertThat(actualItemsList.get(0), equalTo(itemDto1));
        assertThat(actualItemsList.get(1), equalTo(itemDto2));
    }

    @AfterEach
    public void deleteAll() {
        em.createNativeQuery("truncate table users");
        em.createNativeQuery("truncate table items");
    }
}
