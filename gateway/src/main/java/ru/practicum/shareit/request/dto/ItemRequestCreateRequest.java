package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

/**
 * Параметры запроса для создания запроса на вещь.
 */
@Getter
@Setter
@Builder
@Jacksonized
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestCreateRequest {
    @NotBlank
    String description;
}
