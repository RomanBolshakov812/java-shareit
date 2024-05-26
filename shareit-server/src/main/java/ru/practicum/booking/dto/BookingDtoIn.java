package ru.practicum.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
