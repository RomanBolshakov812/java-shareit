package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Integer ownerId);

    ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer ownerId);

    List<ItemDto> getItemsByOwnerId(Integer ownerId);

    ItemDto getItem(Integer itemId, Integer sharerId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(CommentDto commentDto, Integer itemId, Integer bookerId);
}
