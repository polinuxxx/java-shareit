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
 * Тесты для {@link BookingCreateRequest}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateRequestTest {

    private final JacksonTester<BookingCreateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        BookingCreateRequest request = BookingCreateRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .build();

        JsonContent<BookingCreateRequest> result = json.write(request);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(request.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(request.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(request.getEnd().toString());
    }
}