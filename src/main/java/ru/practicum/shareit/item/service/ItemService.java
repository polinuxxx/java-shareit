package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.internal.ItemModel;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

/**
 * Сервис для {@link Item}.
 */
public interface ItemService {
    Item create(Long userId, Item item);

    Item patch(Long userId, Long id, Item item);

    ItemModel getById(Long userId, Long id);

    List<ItemModel> getByUserId(Long userId);

    List<Item> search(String text);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
