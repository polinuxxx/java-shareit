package ru.practicum.shareit.item.internal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры ответа сервера для {@link Comment} (промежуточный слой).
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentModel {
    Long id;

    String text;

    String authorName;
}
