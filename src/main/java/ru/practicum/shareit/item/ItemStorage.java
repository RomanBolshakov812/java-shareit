package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    public Item createItem(Item item);

    public Item updateItem(Item item);

    public List<Item> getItemsByOwnerId(Integer ownerId);

    public Item getItem(Integer itemId);

    public List<Item> searchItem(String text);
}
