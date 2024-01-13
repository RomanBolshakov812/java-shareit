package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDtoOut {

    private Integer id;
    private String description;
    private LocalDateTime created;
}
