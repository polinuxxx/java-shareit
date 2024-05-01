package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Параметры запроса для создания вещи.
 */
@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateRequest {
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Boolean available;

    Long requestId;
}
