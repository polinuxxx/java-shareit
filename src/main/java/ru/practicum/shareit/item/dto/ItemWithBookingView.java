package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class ItemWithBookingView {

    @JsonUnwrapped
    ItemView item;

    BookingView lastBooking;

    BookingView nextBooking;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @SuperBuilder
    public static class BookingView {
        Long id;

        Long bookerId;

        LocalDateTime start;

        LocalDateTime end;
    }
}
