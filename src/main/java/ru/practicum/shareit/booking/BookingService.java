package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

public interface BookingService {

    public BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, Integer bookerId);

    public BookingDtoOut updateBooking(Integer bookingId, Integer ownerId, String approved);

    public BookingDtoOut getBooking(Integer bookingId, Integer sharerId);

    public List<BookingDtoOut> getBookingsByBookerId(Integer bookerId, Integer from,
                                                     Integer size, String state);

    public List<BookingDtoOut> getBookingsByOwnerId(Integer ownerId, Integer from,
                                                    Integer size, String state);
}
