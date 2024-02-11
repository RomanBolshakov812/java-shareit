package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingDtoIn bookingDtoIn, Integer bookerId) {
        return post("", bookerId, bookingDtoIn);
    }

    public ResponseEntity<Object> updateBooking(Integer bookingId, Integer userId,
                                                String approved) {
        return patch("/" + bookingId, userId, approved);
    }

    public ResponseEntity<Object> getBooking(Integer bookingId, Integer sharerId) {
        return get("/" + bookingId, sharerId);
    }

    public ResponseEntity<Object> getBookingsByBookerId(Integer bookerId, Integer from,
                                                        Integer size, String state) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingsByOwnerId(Integer ownerId, Integer from,
                                                        Integer size, String state) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}
