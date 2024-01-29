package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class ItemView {
    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

    List<CommentView> comments;
}
