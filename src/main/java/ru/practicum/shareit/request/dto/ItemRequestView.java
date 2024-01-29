package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Параметры ответа для {@link ItemRequest}.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
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
    @SuperBuilder
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
