package ru.practicum.request;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;

@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<RequestDtoIn> dtoInJson;
    @Autowired
    private JacksonTester<RequestDtoOut> dtoOutJson;
    private final LocalDateTime created = LocalDateTime.parse("2023-01-01T01:01:01");

    // RequestDtoIn
    @Test
    void testSerializeRequestDtoIn() throws Exception {
        RequestDtoIn requestDtoIn = new RequestDtoIn(
                1,
                "description",
                created);

        JsonContent<RequestDtoIn> result = dtoInJson.write(requestDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T01:01:01");
    }

    @Test
    void testDeserializeRequestDtoIn() throws Exception {
        String content = "{\"id\":1,\"description\":\"description\",\"created\":"
                + "\"2023-01-01T01:01:01\"}";

        assertThat(this.dtoInJson.parse(content).getObject())
                .isEqualTo(new RequestDtoIn(
                        1,
                        "description",
                        LocalDateTime.parse("2023-01-01T01:01:01")));
    }

    // RequestDtoOut
    @Test
    void testSerializeRequestDtoOut() throws Exception {
        RequestDtoOut requestDtoOut = new RequestDtoOut(
                1,
                "description",
                created,
                null);

        JsonContent<RequestDtoOut> result = dtoOutJson.write(requestDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.items")
                .isEqualTo(null);
    }

    @Test
    void testDeserializeRequestDtoOut() throws Exception {
        String content = "{\"id\":1,\"description\":\"description\",\"created\":"
                + "\"2023-01-01T01:01:01\",\"items\":null}";

        assertThat(this.dtoOutJson.parse(content).getObject())
                .isEqualTo(new RequestDtoOut(
                        1,
                        "description",
                        LocalDateTime.parse("2023-01-01T01:01:01"),
                        null));
    }
}
