package ru.practicum.shareit.item;

import java.util.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private int generatedId = 1;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        Integer itemId = generatedId++;
        Item item = ItemMapper.toItem(itemDto, itemId, ownerId);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Item updateItem(Item item) {
        Integer itemId = item.getId();
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer ownerId) {
        List<ItemDto> itemsOfOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwnerId(), ownerId)) {
                itemsOfOwner.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsOfOwner;
    }

    @Override
    public Item getItem(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        String textInLowerCase = text.toLowerCase();
        List<ItemDto> foundItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items.values()) {
                if (item.getName().toLowerCase().contains(textInLowerCase)
                        || item.getDescription().toLowerCase().contains(textInLowerCase)) {
                    if (item.getAvailable()) {
                        foundItems.add(ItemMapper.toItemDto(item));
                    }
                }
            }
        }
        return foundItems;
    }
}
