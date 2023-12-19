package ru.practicum.shareit.booking.model;

import lombok.Getter;

/**
 * Статус бронирования.
 */
@Getter
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
