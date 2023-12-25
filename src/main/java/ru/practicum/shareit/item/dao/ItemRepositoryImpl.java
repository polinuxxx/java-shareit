package ru.practicum.shareit.item.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.OperationConstraintException;
import ru.practicum.shareit.item.model.Item;

/**
 * Реализация ДАО для {@link Item}.
 */
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private long counter;

    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(++counter);

        List<Item> userItems;
        if (!items.containsKey(item.getOwner().getId())) {
            userItems = new ArrayList<>();
        } else {
            userItems = getByUserId(item.getOwner().getId());
        }

        userItems.add(item);

        items.put(item.getOwner().getId(), userItems);

        return item;
    }

    @Override
    public Item patch(Item item) {
        Item currentItem = getById(item.getId());

        if (currentItem == null) {
            throw new EntityNotFoundException("Не найдена вещь по id = " + item.getId());
        }
        checkOwner(item.getOwner().getId(), currentItem.getOwner().getId());

        currentItem.setName(item.getName() != null && !item.getName().isBlank() ?
                item.getName() : currentItem.getName());
        currentItem.setDescription(item.getDescription() != null && !item.getDescription().isBlank() ?
                item.getDescription() : currentItem.getDescription());
        currentItem.setAvailable(item.getAvailable() != null ? item.getAvailable() : currentItem.getAvailable());

        return currentItem;
    }

    @Override
    public Item getById(Long id) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return items.get(userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String pattern = "^.*" + text.toLowerCase() + ".*$";

        return items.values().stream().flatMap(Collection::stream)
                .filter(item -> (item.getName().toLowerCase().matches(pattern)
                                || item.getDescription().toLowerCase().matches(pattern))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    private void checkOwner(Long newOwnerId, Long oldOwnerId) {
        if (!newOwnerId.equals(oldOwnerId)) {
            throw new OperationConstraintException("Пользователю " + newOwnerId + " запрещено редактирование чужой вещи");
        }
    }
}
