package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDtoOut {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
