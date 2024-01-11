package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

/**
 * Сервис для {@link Booking}.
 */
public interface BookingService {
    Booking create(Long userId, Booking booking);

    Booking patch(Long userId, Long bookingId, Boolean approved);

    Booking getById(Long userId, Long bookingId);

    List<Booking> getAllByBookerId(Long userId, BookingState state);

    List<Booking> getAllByOwnerId(Long userId, BookingState state);
}
