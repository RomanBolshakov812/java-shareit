package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    public ItemDto addItem(ItemDto itemDto, Integer ownerId);

    public ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer ownerId);

    public List<ItemDto> getItemsByOwnerId(Integer ownerId);

    public ItemDto getItem(Integer itemId, Integer sharerId);

    public List<ItemDto> searchItem(String text);

    public CommentDto addComment(CommentDto commentDto, Integer itemId, Integer bookerId);
}
