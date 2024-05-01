package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link ItemWithBookingView}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemWithBookingViewTest {
    private final JacksonTester<ItemWithBookingView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemWithBookingView view = ItemWithBookingView.builder()
                .item(ItemView.builder()
                        .id(1L)
                        .name("Dyson Supersonic")
                        .description("Topaz orange")
                        .available(true)
                        .requestId(1L)
                        .comments(List.of(
                                CommentView.builder()
                                        .id(1L)
                                        .text("It's a good thing")
                                        .authorName("Denis")
                                        .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                        .build()))
                        .build())
                .nextBooking(ItemWithBookingView.BookingView.builder()
                        .id(1L)
                        .bookerId(1L)
                        .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .lastBooking(ItemWithBookingView.BookingView.builder()
                        .id(2L)
                        .bookerId(2L)
                        .start(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .build();
        JsonContent<ItemWithBookingView> result = json.write(view);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.comments[0].id");
        assertThat(result).hasJsonPath("$.comments[0].text");
        assertThat(result).hasJsonPath("$.comments[0].authorName");
        assertThat(result).hasJsonPath("$.comments[0].created");

        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.lastBooking.id");
        assertThat(result).hasJsonPath("$.lastBooking.bookerId");
        assertThat(result).hasJsonPath("$.lastBooking.start");
        assertThat(result).hasJsonPath("$.lastBooking.end");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.nextBooking.id");
        assertThat(result).hasJsonPath("$.nextBooking.bookerId");
        assertThat(result).hasJsonPath("$.nextBooking.start");
        assertThat(result).hasJsonPath("$.nextBooking.end");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(view.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(view.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(view.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(view.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(view.getItem().getRequestId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(view.getItem().getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(view.getItem().getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(view.getItem().getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(view.getItem().getComments().get(0).getCreationDate().toString());

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(view.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(view.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(view.getLastBooking().getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(view.getLastBooking().getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(view.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(view.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(view.getNextBooking().getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(view.getNextBooking().getEnd().toString());
    }
}