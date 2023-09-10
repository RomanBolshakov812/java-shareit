package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    public Item addItem(ItemDto itemDto, Integer ownerId);

    public Item updateItem(Integer itemId, Item item, Integer ownerId);

    public void deleteItem(Integer itemId);

    public List<ItemDto> getItemsByOwnerId(Integer ownerId);

    public ItemDto getItem(Integer itemId);

    public List<ItemDto> searchItem(String text);
}
