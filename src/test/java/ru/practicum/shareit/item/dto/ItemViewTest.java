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
 * Тесты для {@link ItemView}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemViewTest {
    private final JacksonTester<ItemView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemView view = ItemView.builder()
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
                .build();
        JsonContent<ItemView> result = json.write(view);

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

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(view.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(view.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(view.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(view.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(view.getRequestId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(view.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(view.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(view.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(view.getComments().get(0).getCreationDate().toString());
    }
}