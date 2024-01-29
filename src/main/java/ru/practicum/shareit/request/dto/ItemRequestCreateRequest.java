package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Параметры запроса для создания {@link ItemRequest}
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestCreateRequest {
    @NotBlank
    String description;
}
