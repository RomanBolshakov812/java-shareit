package ru.practicum.item.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingDtoShort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Integer id;
    @NotBlank(message = "Отсутствует название вещи!")
    private String name;
    @NotBlank(message = "Отсутствует описание вещи!")
    private String description;
    @NotNull(message = "Отсутствует статус вещи!")
    private Boolean available;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
