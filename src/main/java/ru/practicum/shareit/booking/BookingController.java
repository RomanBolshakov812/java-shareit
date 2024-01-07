package ru.practicum.shareit.booking;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@Valid @RequestBody BookingDtoIn bookingDtoIn,
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
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBookerId(bookerId, from, size, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwnerId(ownerId, from, size, state);
    }
}
