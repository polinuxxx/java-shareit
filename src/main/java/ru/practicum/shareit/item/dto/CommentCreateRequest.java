package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры запроса для создания {@link Comment}.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    @NotBlank
    String text;
}
