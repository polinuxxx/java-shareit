package ru.practicum.shareit.booking.model;

/**
 * Статус бронирования для пользовательского запроса.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
