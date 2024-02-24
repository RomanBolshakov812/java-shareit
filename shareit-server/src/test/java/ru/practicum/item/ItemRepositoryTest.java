package ru.practicum.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.item.model.Item;

@DataJpaTest
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    public void beforeEach() {
        item1 = itemRepository.save(new Item(null, "Шуруповерт",
                "С аккумулятором", true, 1, null));

        item2 = itemRepository.save(new Item(null, "Пила",
                "Пилит все, без АККУМУЛЯТОРА", true, 1, null));

        item3 = itemRepository.save(new Item(null, "Метла",
                "По металлу, АККУМУЛЯТОРНАЯ", true, 2, null));
    }

    @Test
    public  void findAllItemsByOwnerIdOrderById() {
        List<Item> expectedItems = new ArrayList<>(Arrays.asList(item1, item2));

        List<Item> actualItems = itemRepository.findAllItemsByOwnerIdOrderById(1);

       assertEquals(expectedItems, actualItems);
    }

    @Test
    public void findByDescriptionContainingIgnoreCaseAndAvailableTest() {
        List<Item> expectedItems = new ArrayList<>(Collections.singletonList(item3));

        List<Item> actualItems = itemRepository
                .findByDescriptionContainingIgnoreCaseAndAvailable("мЕТалл", true);

        assertEquals(expectedItems, actualItems);
    }

    @AfterEach
    public void deleteAll() {
        itemRepository.deleteAll();
    }
}
