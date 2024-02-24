package ru.practicum.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "Неверное имя пользователя!")
    private String name;
    @NotBlank(message = "Отсутствует электронная почта!")
    @Email(message = "Неверная электронная почта!")
    private String email;
}
