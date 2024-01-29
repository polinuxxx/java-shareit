package ru.practicum.shareit.request.internal;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Параметры ответа сервера для {@link ItemRequest} с данными о вещах (промежуточный слой).
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class ItemRequestModel {
    Long id;

    String description;

    LocalDateTime creationDate;

    List<ItemModel> items;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @SuperBuilder
    public static class ItemModel {
        Long id;

        String name;

        String description;

        Boolean available;

        Long requestId;
    }
}
