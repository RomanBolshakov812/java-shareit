package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

public interface BookingService {

    BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, Integer bookerId);

    BookingDtoOut updateBooking(Integer bookingId, Integer ownerId, String approved);

    BookingDtoOut getBooking(Integer bookingId, Integer sharerId);

    List<BookingDtoOut> getBookingsByBookerId(Integer bookerId, Integer from,
                                                     Integer size, String state);

    List<BookingDtoOut> getBookingsByOwnerId(Integer ownerId, Integer from,
                                                    Integer size, String state);
}
