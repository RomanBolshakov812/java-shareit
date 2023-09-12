package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    public ItemDto createItem(ItemDto itemDto, Integer ownerId);

    public Item updateItem(Item item);

    public List<ItemDto> getItemsByOwnerId(Integer ownerId);

    public Item getItem(Integer itemId);

    public List<ItemDto> searchItem(String text);
}
