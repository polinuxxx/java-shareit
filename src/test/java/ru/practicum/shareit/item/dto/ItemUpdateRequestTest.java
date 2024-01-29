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
 * Тесты для {@link ItemUpdateRequest}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemUpdateRequestTest {
    private final JacksonTester<ItemUpdateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemUpdateRequest request = ItemUpdateRequest.builder()
                .name("Dyson Supersonic")
                .description("Topaz orange")
                .available(true)
                .build();
        JsonContent<ItemUpdateRequest> result = json.write(request);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(request.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(request.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(request.getAvailable());
    }
}