package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoIn {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}