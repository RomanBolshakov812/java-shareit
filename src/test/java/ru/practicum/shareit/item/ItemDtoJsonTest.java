package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private final BookingDtoShort lastBooking = new BookingDtoShort(1, 1);
    private final BookingDtoShort nextBooking = new BookingDtoShort(2, 2);
    private final ItemDto itemDto = new ItemDto(1, "item1", "item1 description",
            true, lastBooking, nextBooking, null, 1);
    private final LocalDateTime create = LocalDateTime.parse("2023-01-01T01:01:01");
    private final CommentDto commentDto = new CommentDto(1, "Comment", itemDto,
            "Петр", create);
    private final List<CommentDto> commentsList = new ArrayList<>(List.of(commentDto));

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1,
                "item1",
                "item1 description",
                true,
                null,
                null,
                null,
                1);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item1 description");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.comments")
                .isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(1);
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String content = "{\"id\":1,\"name\":\"item1\",\"description\":\"item1 description\","
                + "\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":1},\"nextBooking\""
                + ":{\"id\":2,\"bookerId\":2},\"comments\":[{\"id\":1,\"text\":\"Comment\","
                + "\"item\":{\"id\":1,\"name\":\"item1\",\"description\":\"item1 description\","
                + "\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":1},\"nextBooking\""
                + ":{\"id\":2,\"bookerId\":2},\"comments\":null,\"requestId\":1},\"authorName\""
                + ":\"Петр\",\"created\":\"2023-01-01T01:01:01\"}],\"requestId\":1}";

        assertThat(this.json.parse(content).getObject())
                .isEqualTo(new ItemDto(
                        1,
                        "item1",
                        "item1 description",
                        true,
                        lastBooking,
                        nextBooking,
                        commentsList,
                        1));
    }
}
