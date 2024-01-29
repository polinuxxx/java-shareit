package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры запроса для создания {@link Item}.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
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
