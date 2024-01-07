package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

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

        Item item1 = itemRepository
                .save(new Item(null, "Шуруповерт", "С аккумулятором",
                        true, user1.getId(), null));
        Item item2 = itemRepository
                .save(new Item(null, "Пила", "Пилит все, без АККУМУЛЯТОРА",
                        true, user1.getId(), null));
        Item item3 = itemRepository
                .save(new Item(null, "Метла", "По металлу, АККУМУЛЯТОРНАЯ",
                        false, user2.getId(), null));
    }

    @Test
    public void getItemByIdTest() {
        Optional<Item> item = itemRepository.getItemById(1);
        assertEquals(1, item.get().getId());
    }

    @Test
    public  void findItemByOwnerIdOrderByIdTest() {
        List<Item> items = itemRepository.findItemByOwnerIdOrderById(1);
        assertEquals(2, items.size());
        assertEquals(1, items.get(0).getId());
    }

    @Test
    public void findByDescriptionContainingIgnoreCaseAndAvailableTest() {
        List<Item> items = itemRepository
                .findByDescriptionContainingIgnoreCaseAndAvailable("аККумул", true);
    }
}