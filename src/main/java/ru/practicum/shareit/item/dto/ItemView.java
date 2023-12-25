package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemView {
    Long id;

    String name;

    String description;

    Boolean available;
}
