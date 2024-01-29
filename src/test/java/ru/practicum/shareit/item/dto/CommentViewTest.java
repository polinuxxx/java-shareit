package ru.practicum.shareit.item.dto;

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
 * Тесты для {@link CommentView}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentViewTest {
    private final JacksonTester<CommentView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        CommentView view = CommentView.builder()
                .id(1L)
                .text("It' a good thing")
                .authorName("Denis")
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        JsonContent<CommentView> result = json.write(view);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(view.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(view.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(view.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(view.getCreationDate().toString());
    }
}