package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры запроса для создания {@link Item}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateRequest {
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Boolean available;
}
