package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoShort {
    private Integer id;
    private Integer bookerId;
}
