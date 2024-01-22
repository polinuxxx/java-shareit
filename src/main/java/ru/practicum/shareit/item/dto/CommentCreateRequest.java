package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры запроса для создания {@link Comment}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    @NotBlank
    String text;
}
