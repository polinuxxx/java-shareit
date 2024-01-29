package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры запроса для редактирования {@link Item}.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemUpdateRequest {

    String name;

    String description;

    Boolean available;
}
