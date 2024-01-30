package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingView {

    @JsonUnwrapped
    ItemView item;

    BookingView lastBooking;

    BookingView nextBooking;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class BookingView {
        Long id;

        Long bookerId;

        LocalDateTime start;

        LocalDateTime end;
    }
}
