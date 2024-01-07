package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDtoIn {

    private Integer id;
    @NotBlank(message = "Отсутствует описание вещи!")
    private String description;
    private LocalDateTime created;
}
