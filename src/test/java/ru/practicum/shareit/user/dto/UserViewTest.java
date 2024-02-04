package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для {@link UserView}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserViewTest {
    private final JacksonTester<UserView> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        UserView view = UserView.builder()
                .id(1L)
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();

        JsonContent<UserView> result = json.write(view);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(view.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(view.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(view.getEmail());
    }
}