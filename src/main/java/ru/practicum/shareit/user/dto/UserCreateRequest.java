package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.model.User;

/**
 * Параметры запроса для создания {@link User}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    String name;

    @NotBlank
    @Email
    String email;
}
