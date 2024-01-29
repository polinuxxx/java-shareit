package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Comment;

/**
 * Параметры ответа для {@link Comment}.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentView {
    Long id;

    String text;

    String authorName;

    @JsonProperty("created")
    LocalDateTime creationDate;
}
