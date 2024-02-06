package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemView {
    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

    List<CommentView> comments;
}
