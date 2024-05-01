package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Параметры запроса для создания {@link ItemRequest}
 */
@Getter
@Setter
@Builder
@Jacksonized
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestCreateRequest {
    String description;
}
