package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.model.Item;

/**
 * Параметры ответа для {@link Item}.
 */
@Getter
@Setter
@NoArgsConstructor
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
