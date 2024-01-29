package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.model.User;

/**
 * Параметры запроса для редактирования {@link User}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String name;

    @Email
    String email;
}
