package ru.practicum.booking;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDtoIn;
import ru.practicum.booking.dto.BookingDtoOut;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestBody BookingDtoIn bookingDtoIn,
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
            @RequestParam(value = "from") Integer from,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "state") String state) {
        return bookingService.getBookingsByBookerId(bookerId, from, size, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "from") Integer from,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "state") String state) {
        return bookingService.getBookingsByOwnerId(ownerId, from, size, state);
    }
}
