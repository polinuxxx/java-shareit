package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

/**
 * Сервис для {@link Item}.
 */
public interface ItemService {
    Item create(Long userId, Item item);

    Item patch(Long userId, Long id, Item item);

    Item getById(Long id);

    List<Item> getByUserId(Long userId);

    List<Item> search(String text);
}
