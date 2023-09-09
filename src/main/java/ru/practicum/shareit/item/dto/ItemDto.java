package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ItemDto {

    public ItemDto(String name, String description, Boolean isAvailable, Integer requestId) {
    }
}
