package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {

    private Integer id;
    @NotBlank(message = "Отсутствует текст комментария!")
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
