package ru.practicum.item;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private final LocalDateTime created = LocalDateTime.parse("2023-01-01T01:01:01");
    private final ItemDto item = new ItemDto(1, "item", "description",
            true, null, null, null, 1);

    @Test
    void testSerializeCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "text",
                item,
                "Петя",
                created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("text");
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T01:01:01");
    }

    @Test
    void testDeserializeCommentDto() throws Exception {
        String content = "{\"id\":1,\"text\":\"text\",\"item\":{\"id\":1,\"name\":\"item\","
                + "\"description\":\"description\",\"available\":true,\"lastBooking\":null,"
                + "\"nextBooking\":null,\"comments\":null,\"requestId\":1},\"authorName\":"
                + "\"Петя\",\"created\":\"2023-01-01T01:01:01\"}";

        assertThat(this.json.parse(content).getObject())
                .isEqualTo(new CommentDto(
                        1,
                        "text",
                        item,
                        "Петя",
                        LocalDateTime.parse("2023-01-01T01:01:01")));
    }
}
