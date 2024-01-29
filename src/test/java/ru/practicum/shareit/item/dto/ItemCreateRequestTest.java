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
 * Тесты для {@link ItemCreateRequest}
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateRequestTest {
    private final JacksonTester<ItemCreateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemCreateRequest request = ItemCreateRequest.builder()
                .name("Dyson Supersonic")
                .description("Topaz orange")
                .available(true)
                .requestId(1L)
                .build();
        JsonContent<ItemCreateRequest> result = json.write(request);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(request.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(request.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(request.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(request.getRequestId().intValue());
    }
}