package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.base.AbstractEntity;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.internal.BookingModel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.OperationConstraintException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.internal.CommentModel;
import ru.practicum.shareit.item.internal.ItemModel;
import ru.practicum.shareit.item.mapper.ItemModelConverter;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link Item}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemModelConverter itemModelConverter;

    @Override
    @Transactional
    public Item create(Long userId, Item item) {
        log.info("Добавление вещи {} пользователем с id = {}", item, userId);

        checkUserExists(userId);
        item.setOwner(User.builder().id(userId).build());

        return itemRepository.save(item).toBuilder().build();
    }

    @Override
    @Transactional
    public Item patch(Long userId, Long id, Item item) {
        log.info("Редактирование вещи {} пользователем с id = {}", item, userId);

        checkUserExists(userId);
        item.setId(id);
        item.setOwner(User.builder().id(userId).build());

        Item currentItem = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь по id = " + id));
        checkOwner(item.getOwner().getId(), currentItem.getOwner().getId());

        currentItem.setName(item.getName() != null && !item.getName().isBlank() ?
                item.getName() : currentItem.getName());
        currentItem.setDescription(item.getDescription() != null && !item.getDescription().isBlank() ?
                item.getDescription() : currentItem.getDescription());
        currentItem.setAvailable(item.getAvailable() != null ? item.getAvailable() : currentItem.getAvailable());

        return itemRepository.save(currentItem).toBuilder().build();
    }

    @Override
    public ItemModel getById(Long userId, Long id) {
        log.info("Получение вещи по id = {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь по id = " + id));
        Booking lastBooking = item.getOwner().getId().equals(userId) ? bookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatusIsOrderByStartDesc(id, LocalDateTime.now(),
                        BookingStatus.APPROVED) : null;
        Booking nextBooking = item.getOwner().getId().equals(userId) ? bookingRepository
                .findFirstByItemIdAndStartGreaterThanAndStatusIsOrderByStartAsc(id, LocalDateTime.now(),
                        BookingStatus.APPROVED) : null;
        List<Comment> comments = commentRepository.findByItemId(id);

        return itemModelConverter.convert(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemModel> getByUserId(Long userId) {
        log.info("Получение вещи по id пользователя = {}", userId);

        checkUserExists(userId);

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<Long> itemIds = items.stream().map(AbstractEntity::getId).collect(Collectors.toList());

        Map<Long, Booking> lastBookings = bookingRepository.findAllLastBookings(itemIds, LocalDateTime.now(),
                BookingStatus.APPROVED);
        Map<Long, Booking> nextBookings = bookingRepository.findAllNextBookings(itemIds, LocalDateTime.now(),
                BookingStatus.APPROVED);
        Map<Long, List<Comment>> comments = commentRepository.findByItemIds(itemIds);

        List<ItemModel> views = new ArrayList<>();
        items.forEach(item -> {
            BookingModel lastBooking = item.getOwner().getId().equals(userId) ?
                    itemModelConverter.convert(lastBookings.get(item.getId())) : null;
            BookingModel nextBooking = item.getOwner().getId().equals(userId) ?
                    itemModelConverter.convert(nextBookings.get(item.getId())) : null;
            List<CommentModel> commentModels = itemModelConverter.convertComments(comments.get(item.getId()));

            ItemModel view = ItemModel.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .comments(commentModels)
                    .lastBooking(lastBooking)
                    .nextBooking(nextBooking)
                    .build();
            views.add(view);
        });

        return views;
    }

    @Override
    public List<Item> search(String text) {
        log.info("Поиск вещи, содержащей в названии или описании {}", text);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(itemRepository.search(text));
    }

    @Override
    @Transactional
    public Comment createComment(Long userId, Long itemId, Comment comment) {
        log.info("Добавление отзыва на вещь с id {} пользователем с id = {}", itemId, userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь по id = " + userId));
        checkItemExists(itemId);

        Booking userBooking = bookingRepository
                .findFirstByBookerIdAndItemIdAndEndLessThan(userId, itemId, LocalDateTime.now());

        if (userBooking == null) {
            throw new ValidationException("Пользователю " + userId +
                    " запрещено оставлять отзывы на вещь, которую он не брал в аренду");
        }
        comment.setAuthor(author);
        comment.setItem(Item.builder().id(itemId).build());

        return commentRepository.save(comment).toBuilder().build();
    }

    private void checkItemExists(Long itemId) {
        if (itemId != null && !itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("Не найдена вещь по id = " + itemId);
        }
    }

    private void checkUserExists(Long userId) {
        if (userId != null && !userRepository.existsById(userId)) {
           throw new EntityNotFoundException("Не найден пользователь по id = " + userId);
        }
    }

    private void checkOwner(Long newOwnerId, Long oldOwnerId) {
        if (!newOwnerId.equals(oldOwnerId)) {
            throw new OperationConstraintException("Пользователю " + newOwnerId +
                    " запрещено редактирование чужой вещи");
        }
    }
}
