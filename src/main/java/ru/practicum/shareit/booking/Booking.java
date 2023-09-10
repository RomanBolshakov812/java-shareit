package ru.practicum.shareit.booking;

import java.time.LocalDate;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
public class Booking {
    private Integer id; //— уникальный идентификатор бронирования;
    private LocalDate start; //— дата и время начала бронирования;
    private LocalDate end; //— дата и время конца бронирования;
    private Item item; //— вещь, которую пользователь бронирует;
    private User booker; //— пользователь, который осуществляет бронирование;
    BookingStatus status; //— статус бронирования.
}
