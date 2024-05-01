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
 * Тесты для {@link UserCreateRequest}.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCreateRequestTest {
    private final JacksonTester<UserCreateRequest> json;

    @Test
    @SneakyThrows
    void testSerialization() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();

        JsonContent<UserCreateRequest> result = json.write(request);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(request.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(request.getEmail());
    }
}