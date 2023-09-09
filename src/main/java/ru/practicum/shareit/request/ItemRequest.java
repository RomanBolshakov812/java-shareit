package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Integer id; // — уникальный идентификатор запроса;
    private String description; // — текст запроса, содержащий описание требуемой вещи;
    private User requestor; // — пользователь, создавший запрос;
    private LocalDateTime creationDateAndTime; // — дата и время создания запроса.

    public ItemRequest() {
    }
}
