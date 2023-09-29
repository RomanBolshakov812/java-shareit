package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NotNull(message = "Отсутствуют данные создаваемого бронирования!")
public class BookingDtoIn {
    @NotNull(message = "Отсутствует id вещи!")
    private Integer itemId;
    @NotNull(message = "Отсутствует дата начала бронирования!")
    private LocalDateTime start;
    @NotNull(message = "Отсутствует дата окончания бронирования!")
    private LocalDateTime end;
}