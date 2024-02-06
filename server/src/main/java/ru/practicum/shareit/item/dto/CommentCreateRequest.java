package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры запроса для создания {@link Comment}.
 */
@Getter
@Setter
@Builder
@Jacksonized
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    String text;
}
