package ru.practicum.shareit.item.internal;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры ответа сервера для {@link Comment} (промежуточный слой).
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentModel {
    Long id;

    String text;

    String authorName;

    LocalDateTime creationDate;
}
