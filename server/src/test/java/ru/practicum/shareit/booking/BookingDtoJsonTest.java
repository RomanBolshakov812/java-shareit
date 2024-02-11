package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDtoIn> dtoInJson;
    @Autowired
    private JacksonTester<BookingDtoOut> dtoOutJson;
    @Autowired
    private JacksonTester<BookingDtoShort> dtoShortJson;

    private final LocalDateTime start = LocalDateTime.parse("2023-01-01T01:01:01");
    private final LocalDateTime end = LocalDateTime.parse("2023-02-01T01:01:01");
    private final UserDto booker = new UserDto(1, "Booker", "booker@mail.ru");
    private final BookingDtoShort lastBooking = new BookingDtoShort(1, 1);
    private final BookingDtoShort nextBooking = new BookingDtoShort(2, 2);
    private final ItemDto item = new ItemDto(1, "item", "item description",
            true, lastBooking, nextBooking, null, 1);

    // BookingDtoIn
    @Test
    void testSerializeBookingDtoIn() throws Exception {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1,
                start,
                end);

        JsonContent<BookingDtoIn> result = dtoInJson.write(bookingDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-02-01T01:01:01");
    }

    @Test
    void testDeserializeBookingDtoIn() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2023-01-01T01:01:01\","
                + "\"end\":\"2023-02-01T01:01:01\"}";

        assertThat(this.dtoInJson.parse(content).getObject())
                .isEqualTo(new BookingDtoIn(
                        1,
                        LocalDateTime.parse("2023-01-01T01:01:01"),
                        LocalDateTime.parse("2023-02-01T01:01:01")));
    }

    // BookingDtoOut
    @Test
    void testSerializeBookingDtoOut() throws Exception {
        BookingDtoOut bookingDtoOut = new BookingDtoOut(
                1,
                start,
                end,
                item,
                booker,
                BookingState.WAITING);

        JsonContent<BookingDtoOut> result = dtoOutJson.write(bookingDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-02-01T01:01:01");
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("booker@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
    }

    @Test
    void testDeserializeBookingDtoOut() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-01-01T01:01:01\",\"end\":"
                + "\"2023-02-01T01:01:01\",\"item\":{\"id\":1,\"name\":\"item\","
                + "\"description\":\"item description\",\"available\":true,\"lastBooking\":"
                + "{\"id\":1,\"bookerId\":1},\"nextBooking\":{\"id\":2,\"bookerId\":2},"
                + "\"comments\":null,\"requestId\":1},\"booker\":{\"id\":1,\"name\":\"Booker\","
                + "\"email\":\"booker@mail.ru\"},\"status\":\"WAITING\"}";

        assertThat(this.dtoOutJson.parse(content).getObject())
                .isEqualTo(new BookingDtoOut(
                        1,
                        LocalDateTime.parse("2023-01-01T01:01:01"),
                        LocalDateTime.parse("2023-02-01T01:01:01"),
                        item,
                        booker,
                        BookingState.WAITING));
    }

    // BookingDtoShort
    @Test
    void testSerializeBookingDtoShort() throws Exception {
        BookingDtoShort bookingDtoShort = new BookingDtoShort(
                1,
                2);

        JsonContent<BookingDtoShort> result = dtoShortJson.write(bookingDtoShort);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(2);
    }

    @Test
    void testDeserializeBookingDtoShort() throws Exception {
        String content = "{\"id\":1,\"bookerId\":2}";

        assertThat(this.dtoShortJson.parse(content).getObject())
                .isEqualTo(new BookingDtoShort(
                        1,
                        2));
    }
}
