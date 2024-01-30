package ru.practicum.shareit.item.internal;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.internal.BookingModel;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа сервера для {@link Item} с бронированием (промежуточный слой).
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemModel {
    Long id;

    String name;

    String description;

    Boolean available;

    List<CommentModel> comments;

    BookingModel lastBooking;

    BookingModel nextBooking;
}
