package ru.practicum.shareit.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDtoOut toBookingDtoOut(Booking booking) {

        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
                );
    }

    public static BookingDtoShort toBookingDtoShort(Booking booking) {
        return new BookingDtoShort(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static Booking toBookingOfDtoIn(BookingDtoIn bookingDtoIn, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static List<BookingDtoOut> toListBookingDtoOut(List<Booking> bookings) {
        List<BookingDtoOut> bookingsDtoOut = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDtoOut.add(toBookingDtoOut(booking));
        }
        return bookingsDtoOut;
    }
}
