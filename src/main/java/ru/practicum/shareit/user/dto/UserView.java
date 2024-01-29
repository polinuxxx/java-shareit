package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.model.User;

/**
 * Параметры ответа для {@link User}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserView {
    Long id;

    String name;

    String email;
}
