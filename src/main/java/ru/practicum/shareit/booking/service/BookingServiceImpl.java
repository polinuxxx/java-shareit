package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link Booking}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking create(Long userId, Booking booking) {
        log.info("Добавление бронирования {} пользователем с id = {}", booking, userId);

        checkUserExists(userId);
        booking.setBooker(User.builder().id(userId).build());

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("Не найдена вещь по id = " +
                        booking.getItem().getId()));
        booking.setItem(item);

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + booking.getItem().getId() +
                    " не доступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Вещь с id = " + booking.getItem().getId() +
                    " уже принадлежит пользователю с id = " + userId);
        }

        return bookingRepository.save(booking).toBuilder().build();
    }

    @Override
    @Transactional
    public Booking patch(Long userId, Long bookingId, Boolean approved) {
        log.info("Редактирование бронирования с id = {} пользователем с id = {}", bookingId, userId);

        checkUserExists(userId);

        Booking currentBooking = getById(userId, bookingId);
        checkOwner(userId, currentBooking.getItem().getOwner().getId());

        if (currentBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус бронирования с id = " + bookingId + " уже APPROVED");
        }

        if (approved) {
            currentBooking.setStatus(BookingStatus.APPROVED);
        } else {
            currentBooking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(currentBooking).toBuilder().build();
    }

    @Override
    public Booking getById(Long userId, Long bookingId) {
        log.info("Получение бронирования по id = {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдено бронирование по id = " + bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Пользователю " + userId +
                    " запрещен просмотр бронирования " + bookingId);
        }
        return booking.toBuilder().build();
    }

    @Override
    public List<Booking> getAllByBookerId(Long userId, BookingState state) {
        log.info("Получение всех бронирований пользователя с id = {}", userId);
        checkUserExists(userId);

        switch (state) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedOperationException("Статус не поддерживается");
        }
    }

    @Override
    public List<Booking> getAllByOwnerId(Long userId, BookingState state) {
        log.info("Получение всех бронирований по вещам для владельца с id = {}", userId);
        checkUserExists(userId);

        switch (state) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedOperationException("Статус не поддерживается");
        }
    }

    private void checkUserExists(Long userId) {
        if (userId != null && !userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Не найден пользователь по id = " + userId);
        }
    }

    private void checkOwner(Long updateUserId, Long ownerId) {
        if (!updateUserId.equals(ownerId)) {
            throw new EntityNotFoundException("Пользователю " + updateUserId +
                    " запрещено редактирование чужого бронирования");
        }
    }
}
