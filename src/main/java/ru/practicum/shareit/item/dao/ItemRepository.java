package ru.practicum.shareit.item.dao;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

/**
 * ДАО для {@link Item}.
 */
public interface ItemRepository {
    Item create(Item item);

    Item patch(Item item);

    Item getById(Long id);

    List<Item> getByUserId(Long userId);

    List<Item> search(String text);
}
