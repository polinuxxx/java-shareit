package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link CommentCreateRequest}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentCreateRequestTest {
    private final JacksonTester<CommentCreateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        CommentCreateRequest request = CommentCreateRequest.builder()
                .text("It's a good thing")
                .build();
        JsonContent<CommentCreateRequest> result = json.write(request);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(request.getText());
    }
}