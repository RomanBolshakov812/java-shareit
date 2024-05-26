package ru.practicum.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.booking.BookingState;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

@Data
@AllArgsConstructor
public class BookingDtoOut {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingState status;
}
