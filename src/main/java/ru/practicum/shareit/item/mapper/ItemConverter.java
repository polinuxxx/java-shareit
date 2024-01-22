package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;

/**
 * Конвертер для {@link Item}.
 */
@Mapper(componentModel = "spring", uses = CommentConverter.class)
public interface ItemConverter {

    Item convert(ItemCreateRequest request);

    Item convert(ItemUpdateRequest request);

    ItemView convert(Item item);

    List<ItemView> convert(List<Item> items);
}
