package ru.practicum.shareit.request.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.base.AbstractEntity;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.mapper.ItemRequestConverter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link ItemRequest}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestConverter itemRequestConverter;

    @Override
    @Transactional
    public ItemRequest create(Long userId, ItemRequest itemRequest) {
        log.info("Добавление нового запроса на вещь {} пользователем с id = {}", itemRequest, userId);

        checkUserExists(userId);
        itemRequest.setRequestor(User.builder().id(userId).build());

        return itemRequestRepository.save(itemRequest).toBuilder().build();
    }

    @Override
    public List<ItemRequestModel> getByRequestorId(Long userId) {
        log.info("Получение всех запросов на вещи пользователя с id = {}", userId);

        checkUserExists(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreationDateDesc(userId);

        return createItemRequestModelsFromItemRequests(itemRequests);
    }

    @Override
    public List<ItemRequestModel> getByUserId(Long userId, Integer from, Integer size) {
        log.info("Получение всех запросов на вещи с пагинацией");

        checkUserExists(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId,
                PageRequest.of(from / size, size));

        return createItemRequestModelsFromItemRequests(itemRequests);
    }

    @Override
    public ItemRequestModel getById(Long userId, Long id) {
        log.info("Получение запроса на вещь по id запроса = {}", id);

        checkUserExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена запрос на вещь по id = " + id));

        List<Item> items = itemRepository.findByRequestId(id);

        return itemRequestConverter.convert(itemRequest, items);
    }

    private List<ItemRequestModel> createItemRequestModelsFromItemRequests(List<ItemRequest> itemRequests) {
        List<Long> requestIds = itemRequests.stream().map(AbstractEntity::getId).collect(Collectors.toList());

        Map<Long, List<Item>> items = itemRepository.findAllItemsByRequestIds(requestIds);

        List<ItemRequestModel> views = new ArrayList<>();
        itemRequests.forEach(itemRequest -> {
            List<ItemRequestModel.ItemModel> itemModels = items.get(itemRequest.getId()) == null ? new ArrayList<>() :
                    itemRequestConverter.convertItems(items.get(itemRequest.getId()));
            ItemRequestModel view = ItemRequestModel.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .creationDate(itemRequest.getCreationDate())
                    .items(itemModels)
                    .build();
            views.add(view);
        });
        return views;
    }

    private void checkUserExists(Long userId) {
        if (userId != null && !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Не найден пользователь по id = " + userId);
        }
    }
}
