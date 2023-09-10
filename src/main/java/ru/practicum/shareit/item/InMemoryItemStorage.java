package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private int generatedId = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(generatedId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Integer itemId = item.getId();
        items.put(itemId, item);
        return item;
    }

    @Override
    public void deleteItem(Integer itemId) {

    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemsByOwnerId() {
        return null;
    }

    @Override
    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }
}
