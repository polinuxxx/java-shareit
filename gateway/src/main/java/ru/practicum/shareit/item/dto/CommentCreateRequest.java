package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

/**
 * Параметры запроса для создания комментария к вещи.
 */
@Getter
@Setter
@Builder
@ToString
@Jacksonized
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    @NotBlank
    String text;
}
