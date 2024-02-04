package ru.practicum.shareit.booking.internal;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

/**
 * Параметры ответа сервера для {@link Booking} (промежуточный слой).
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingModel {
    Long id;

    Long bookerId;

    LocalDateTime start;

    LocalDateTime end;
}
