package ru.practicum.shareit.booking.internal;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.model.Booking;

/**
 * Параметры ответа сервера для {@link Booking} (промежуточный слой).
 */
@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingModel {
    Long id;

    Long bookerId;

    LocalDateTime start;

    LocalDateTime end;
}
