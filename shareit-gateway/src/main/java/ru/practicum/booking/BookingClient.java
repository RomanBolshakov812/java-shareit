package ru.practicum.booking;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl
                                + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, Integer bookerId) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> updateBooking(Integer bookingId, Integer userId,
                                                String approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(Integer bookingId, Integer sharerId) {
        return get("/" + bookingId, sharerId);
    }

    public ResponseEntity<Object> getBookingsByBookerId(Integer bookerId, Integer from,
                                                        Integer size, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingsByOwnerId(Integer ownerId, Integer from,
                                                        Integer size, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}
