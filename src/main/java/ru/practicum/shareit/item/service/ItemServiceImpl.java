package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link Item}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Item create(Long userId, Item item) {
        log.info("Добавление вещи {} пользователем с id = {}", item, userId);
        checkUserExists(userId);
        item.setOwner(User.builder().id(userId).build());
        return itemRepository.create(item).toBuilder().build();
    }

    @Override
    public Item patch(Long userId, Long id, Item item) {
        log.info("Редактирование вещи {} пользователем с id = {}", item, userId);
        checkUserExists(userId);
        item.setId(id);
        item.setOwner(User.builder().id(userId).build());
        return itemRepository.patch(item).toBuilder().build();
    }

    @Override
    public Item getById(Long id) {
        log.info("Получение вещи по id = {}", id);
        return itemRepository.getById(id).toBuilder().build();
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        log.info("Получение вещи по id пользователя = {}", userId);
        checkUserExists(userId);
        return new ArrayList<>(itemRepository.getByUserId(userId));
    }

    @Override
    public List<Item> search(String text) {
        log.info("Поиск вещи, содержащей в названии или описании {}", text);
        return new ArrayList<>(itemRepository.search(text));
    }

    private void checkUserExists(Long userId) {
        if (userId != null && !userRepository.exists(userId)) {
           throw new EntityNotFoundException("Не найден пользователь по id = " + userId);
        }
    }
}
