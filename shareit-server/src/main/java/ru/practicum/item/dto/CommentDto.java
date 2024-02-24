package ru.practicum.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
