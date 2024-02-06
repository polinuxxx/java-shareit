package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Параметры запроса для создания бронирования.
 */
@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateRequest {
    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @FutureOrPresent
    LocalDateTime end;

    @NotNull
    Long itemId;

    @JsonIgnore
    @AssertTrue
    public boolean isEndAfterStart() {
        return start != null && end != null && end.isAfter(start);
    }
}
