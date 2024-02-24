package ru.practicum.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull(message = "Отсутствует id вещи!")
    private Integer itemId;
    @NotNull(message = "Отсутствует дата начала бронирования!")
    @FutureOrPresent(message = "Неверные даты бронирования!")
    private LocalDateTime start;
    @NotNull(message = "Отсутствует дата окончания бронирования!")
    @Future(message = "Неверные даты бронирования!")
    private LocalDateTime end;
}
