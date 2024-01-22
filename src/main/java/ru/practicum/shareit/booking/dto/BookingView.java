package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

/**
 * Параметры ответа для {@link Booking}.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingView {
    Long id;

    LocalDateTime start;

    LocalDateTime end;

    ItemView item;

    BookerView booker;

    String status;

    /**
     * Вложенное вью для пользователя, осуществляющего бронирование.
     */
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookerView {
        Long id;
    }

    /**
     * Вложенное вью для вещи.
     */
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ItemView {
        Long id;

        String name;
    }
}
