package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NotNull(message = "Отсутствуют данные пользователя!")
public class UserDto {
    private Integer id;
    @NotBlank(message = "Неверное имя пользователя!")
    private String name;
    @NotBlank(message = "Отсутствует электронная почта!")
    @Email(message = "Неверная электронная почта!")
    private String email;
}
