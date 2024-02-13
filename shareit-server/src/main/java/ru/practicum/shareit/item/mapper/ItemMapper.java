package ru.practicum.shareit.item.mapper;

import java.util.ArrayList;
import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Integer requestId;
        Request request = item.getRequest();
        if (request != null) {
            requestId = request.getId();
        } else {
            requestId = null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                requestId
        );
    }

    public static Item toItem(ItemDto itemDto, Integer itemOwner, Request request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemOwner);
        item.setRequest(request);
        return item;
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        if (items != null) {
            for (Item item : items) {
                itemsDto.add(toItemDto(item));
            }
        }
        return itemsDto;
    }
}
