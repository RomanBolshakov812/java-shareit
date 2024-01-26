package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest()
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1,
                "Петр Петров",
                "petr@mail.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Петр Петров");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petr@mail.ru");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String content = "{\"id\": 1,\"name\": \"Петр Петров\",\"email\": \"petr@mail.ru\"}";

        assertThat(this.json.parse(content).getObject())
                .isEqualTo(new UserDto(
                        1,
                        "Петр Петров",
                        "petr@mail.ru"));
    }
}
