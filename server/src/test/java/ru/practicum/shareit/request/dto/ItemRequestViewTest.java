package ru.practicum.shareit.request.dto;

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
 * Параметры ответа для {@link ItemRequestView}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestViewTest {
    private final JacksonTester<ItemRequestView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemRequestView view = ItemRequestView.builder()
                .id(1L)
                .description("I want to rent Dyson Supersonic")
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .items(List.of(
                        ItemRequestView.ItemView.builder()
                                .id(1L)
                                .name("Dyson Supersonic")
                                .description("Topaz orange")
                                .available(true)
                                .requestId(1L)
                                .build(),
                        ItemRequestView.ItemView.builder()
                                .id(2L)
                                .name("Dyson Supersonic")
                                .description("Copper")
                                .available(true)
                                .requestId(1L)
                                .build()))
                .build();
        JsonContent<ItemRequestView> result = json.write(view);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).hasJsonPath("$.items[0].id");
        assertThat(result).hasJsonPath("$.items[0].name");
        assertThat(result).hasJsonPath("$.items[0].description");
        assertThat(result).hasJsonPath("$.items[0].available");
        assertThat(result).hasJsonPath("$.items[0].requestId");
        assertThat(result).hasJsonPath("$.items[1].id");
        assertThat(result).hasJsonPath("$.items[1].name");
        assertThat(result).hasJsonPath("$.items[1].description");
        assertThat(result).hasJsonPath("$.items[1].available");
        assertThat(result).hasJsonPath("$.items[1].requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(view.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(view.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(view.getCreationDate().toString());

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(view.getItems().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(view.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(view.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(view.getItems().get(0).getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(view.getItems().get(0).getRequestId().intValue());

        assertThat(result).extractingJsonPathNumberValue("$.items[1].id")
                .isEqualTo(view.getItems().get(1).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[1].name")
                .isEqualTo(view.getItems().get(1).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[1].description")
                .isEqualTo(view.getItems().get(1).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[1].available")
                .isEqualTo(view.getItems().get(1).getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[1].requestId")
                .isEqualTo(view.getItems().get(1).getRequestId().intValue());
    }
}