package ru.practicum.shareit.item.mapper;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;

/**
 * Маппер для {@link Item}.
 */
@UtilityClass
public class ItemMapper {
    public static ItemView toItemView(Item item) {
        return new ItemView(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static List<ItemView> toItemViewList(List<Item> items) {
        return items.stream().map(ItemMapper::toItemView).collect(Collectors.toList());
    }

    public static Item toItem(ItemCreateRequest request) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }

    public static Item toItem(ItemUpdateRequest request) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }
}
