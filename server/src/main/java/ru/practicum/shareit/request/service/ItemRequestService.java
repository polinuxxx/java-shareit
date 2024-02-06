package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Сервис для {@link ItemRequest}.
 */
public interface ItemRequestService {
    ItemRequest create(Long userId, ItemRequest itemRequest);

    List<ItemRequestModel> getByRequestorId(Long userId);

    List<ItemRequestModel> getByUserId(Long userId, Integer from, Integer size);

    ItemRequestModel getById(Long userId, Long id);
}
