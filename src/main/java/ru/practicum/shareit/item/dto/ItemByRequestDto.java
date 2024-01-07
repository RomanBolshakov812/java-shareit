package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemByRequestDto {
    private Integer id;
    @NotBlank(message = "Отсутствует название вещи!")
    private String name;
    @NotBlank(message = "Отсутствует описание вещи!")
    private String description;
    @NotNull(message = "Отсутствует статус вещи!")
    private Boolean available;
    @NotBlank(message = "Отсутствует id запроса!")
    private Integer requestId;
}
