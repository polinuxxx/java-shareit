package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link ItemRequestCreateRequest}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestCreateRequestTest {
    private final JacksonTester<ItemRequestCreateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemRequestCreateRequest request = ItemRequestCreateRequest.builder()
                .description("I want to rent Dyson Supersonic")
                .build();
        JsonContent<ItemRequestCreateRequest> result = json.write(request);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(request.getDescription());
    }
}