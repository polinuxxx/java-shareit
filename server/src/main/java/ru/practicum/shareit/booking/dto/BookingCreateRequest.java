package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

/**
 * Параметры запроса для создания {@link Booking}.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateRequest {
    LocalDateTime start;

    LocalDateTime end;

    Long itemId;
}
