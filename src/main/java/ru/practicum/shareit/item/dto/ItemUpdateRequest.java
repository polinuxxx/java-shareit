package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры запроса для редактирования {@link Item}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemUpdateRequest {

    String name;

    String description;

    Boolean available;
}
