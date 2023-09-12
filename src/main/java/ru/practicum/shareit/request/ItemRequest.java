package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@NoArgsConstructor
public class ItemRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime creationDateAndTime;
}
