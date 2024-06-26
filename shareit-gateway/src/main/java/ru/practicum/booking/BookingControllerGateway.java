package ru.practicum.booking;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.exception.UnsupportedStatusException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingControllerGateway {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                        @RequestHeader("X-Sharer-User-Id") Integer bookerId) {
        return bookingClient.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Integer bookingId,
                                       @RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam String approved) {
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Integer bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Integer sharerId) {
        return bookingClient.getBooking(bookingId, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") Integer bookerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        System.out.println();
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Неизвестный статус: "
                        + stateParam));
        return bookingClient.getBookingsByBookerId(bookerId, from, size, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        System.out.println();
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Неизвестный статус: "
                        + stateParam));
        return bookingClient.getBookingsByOwnerId(ownerId, from, size, state);
    }
}
