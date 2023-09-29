package ru.practicum.shareit.booking;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@Valid @RequestBody BookingDtoIn bookingDtoIn,
                                       @NotNull(message = "Отсутствует id пользователя!")
                                       @RequestHeader("X-Sharer-User-Id") Integer bookerId) {
        return bookingService.addBooking(bookingDtoIn, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBooking(@PathVariable Integer bookingId,
                                       @RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam String approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBooking(@PathVariable Integer bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Integer sharerId) {
        return bookingService.getBooking(bookingId, sharerId);
    }

    @GetMapping
    public List<BookingDtoOut> getBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") Integer bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }
}
