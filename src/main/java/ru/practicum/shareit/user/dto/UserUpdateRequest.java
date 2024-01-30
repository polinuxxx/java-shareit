package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

/**
 * Параметры запроса для редактирования {@link User}.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String name;

    @Email
    String email;
}
