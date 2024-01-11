package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры ответа для {@link Comment}.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentView {
    Long id;

    String text;

    String authorName;

    LocalDateTime created;
}
