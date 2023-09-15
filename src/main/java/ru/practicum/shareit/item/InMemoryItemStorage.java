package ru.practicum.shareit.item;

import java.util.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Item>> itemsOfOwner = new HashMap<>();
    private int generatedId = 1;

    @Override
    public Item createItem(Item item) {
        Integer itemId = generatedId++;
        item.setId(itemId);
        items.put(itemId, item);
        List<Item> itemsOfCurrentOwner = new ArrayList<>();
        if (itemsOfOwner.containsKey(item.getOwnerId())) {
            itemsOfCurrentOwner = itemsOfOwner.get(item.getOwnerId());
        }
        itemsOfCurrentOwner.add(item);
        itemsOfOwner.put(item.getOwnerId(), itemsOfCurrentOwner);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Integer itemId = item.getId();
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> getItemsByOwnerId(Integer ownerId) {
        return new ArrayList<>(itemsOfOwner.get(ownerId));
    }

    @Override
    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItem(String text) {
        String textInLowerCase = text.toLowerCase();
        List<Item> foundItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items.values()) {
                if (item.getName().toLowerCase().contains(textInLowerCase)
                        || item.getDescription().toLowerCase().contains(textInLowerCase)) {
                    if (item.getAvailable()) {
                        foundItems.add(item);
                    }
                }
            }
        }
        return foundItems;
    }
}
