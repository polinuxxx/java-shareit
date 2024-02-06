package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link BookingView}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingViewTest {
    private final JacksonTester<BookingView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        BookingView view = BookingView.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .item(BookingView.ItemView.builder()
                        .id(1L)
                        .name("Scissors")
                        .build())
                .booker(BookingView.BookerView.builder()
                        .id(1L)
                        .build())
                .status("APPROVED")
                .build();
        JsonContent<BookingView> result = json.write(view);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).hasJsonPath("$.item.name");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.booker.id");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(view.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(view.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(view.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(view.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(view.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(view.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(view.getStatus());
    }
}