package ru.practicum.shareit.item.mapper;

import java.util.ArrayList;
import java.util.List;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null,
                item.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto, Integer itemOwner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemOwner);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static ItemByRequestDto toItemByRequestDto(Item item) {
        return new ItemByRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static List<ItemByRequestDto> toListItemsByRequestDto(List<Item> items) {
        if (items == null) {
            return null;
        }
        List<ItemByRequestDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemByRequestDto(item));
        }
        return itemsDto;
    }
}
