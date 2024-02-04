package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Параметры ответа для {@link ItemRequest}.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestView {
    Long id;

    String description;

    @JsonProperty("created")
    LocalDateTime creationDate;

    List<ItemView> items;

    /**
     * Вложенное вью для вещи.
     */
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    @Getter
    @Setter
    public static class ItemView {
        Long id;

        String name;

        String description;

        Boolean available;

        Long requestId;
    }
}
