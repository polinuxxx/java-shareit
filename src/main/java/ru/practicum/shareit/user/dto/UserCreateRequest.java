package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

/**
 * Параметры запроса для создания {@link User}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    String name;

    @NotNull
    @Email
    String email;
}
