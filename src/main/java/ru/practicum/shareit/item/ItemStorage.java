package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    public Item createItem(Item item);

    public Item updateItem(Item item);

    public void deleteItem(Integer itemId);

    public List<Item> getAllItems();

    public List<Item> getItemsByOwnerId();

    public Item getItem(Integer itemId);
}
